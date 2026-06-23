package ru.voidrp.dailyquests.listener;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import ru.voidrp.dailyquests.VoidRpDailyQuestsPlugin;
import ru.voidrp.dailyquests.gui.QuestGui;
import ru.voidrp.dailyquests.player.DeliveryQuestStorage;
import ru.voidrp.dailyquests.player.HardQuestStorage;
import ru.voidrp.dailyquests.player.PlayerQuestState;
import ru.voidrp.dailyquests.player.QuestStorage;
import ru.voidrp.dailyquests.quest.ActiveQuest;
import ru.voidrp.dailyquests.quest.QuestType;
import ru.voidrp.dailyquests.tracker.QuestTracker;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public final class QuestProgressListener implements Listener {

    private final QuestStorage         storage;
    private final HardQuestStorage     hard;
    private final DeliveryQuestStorage delivery;
    private final Economy economy;
    private final String newQuestsMsg;
    private final String questCompleteMsg;
    private final Logger log;

    public QuestProgressListener(QuestStorage storage, HardQuestStorage hard,
                                  DeliveryQuestStorage delivery,
                                  Economy economy,
                                  String newQuestsMsg, String questCompleteMsg,
                                  Logger log) {
        this.storage          = storage;
        this.hard             = hard;
        this.delivery         = delivery;
        this.economy          = economy;
        this.newQuestsMsg     = newQuestsMsg;
        this.questCompleteMsg = questCompleteMsg;
        this.log              = log;
    }

    // ── Login: generate today's quests if needed ──────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        boolean fresh = storage.ensureToday(player.getUniqueId());
        if (fresh) {
            player.sendMessage(color(newQuestsMsg));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        storage.evict(e.getPlayer().getUniqueId());
    }

    // ── Kill ──────────────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKill(EntityDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        // getKiller() returns null for indirect kills (fire aspect, poison, arrows in some cases).
        // Fall back to last damage cause to catch those.
        if (killer == null) {
            var dmg = e.getEntity().getLastDamageCause();
            if (dmg instanceof EntityDamageByEntityEvent byEntity) {
                Entity damager = byEntity.getDamager();
                if (damager instanceof Player p) killer = p;
                else if (damager instanceof Projectile proj && proj.getShooter() instanceof Player p) killer = p;
            }
        }
        if (killer == null) return;

        String entityKey = e.getEntity().getType().getKey().toString(); // e.g. "minecraft:zombie"
        trackProgress(killer, QuestType.KILL, entityKey, 1);
    }

    // ── Collect (item pickup) ─────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPickup(PlayerPickupItemEvent e) {
        String material = e.getItem().getItemStack().getType().name(); // e.g. "IRON_INGOT"
        trackProgress(e.getPlayer(), QuestType.COLLECT, material, e.getItem().getItemStack().getAmount());
    }

    // ── Mine ──────────────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        String material = e.getBlock().getType().name();
        trackProgress(e.getPlayer(), QuestType.MINE, material, 1);
    }

    // ── Fish ──────────────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFish(PlayerFishEvent e) {
        if (e.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (e.getCaught() == null) return;

        Entity caught = e.getCaught();
        String materialName = "ANY";
        if (caught instanceof org.bukkit.entity.Item item) {
            materialName = item.getItemStack().getType().name();
        }

        // Match both specific fish type and "ANY"
        PlayerQuestState state = storage.get(e.getPlayer().getUniqueId());
        for (ActiveQuest q : state.quests) {
            if (q.type != QuestType.FISH) continue;
            if (!q.isCompleted() && (q.target.equals("ANY") || q.target.equals(materialName))) {
                awardProgress(e.getPlayer(), q, 1);
            }
        }
        storage.save(e.getPlayer().getUniqueId());
    }

    // ── Breed ─────────────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreed(EntityBreedEvent e) {
        if (!(e.getBreeder() instanceof Player player)) return;
        String entityKey = e.getEntity().getType().getKey().toString();
        trackProgress(player, QuestType.BREED, entityKey, 1);
    }

    // ── GUI click: claim reward ───────────────────────────────────────────────

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (e.getView().getTitle() == null) return;
        if (!e.getView().getTitle().equals(QuestGui.TITLE)) return;

        e.setCancelled(true);

        int slot = e.getRawSlot();

        // Shift+Click on quest item slot = toggle tracker
        int[] questSlots = {10, 13, 16};
        for (int i = 0; i < questSlots.length; i++) {
            if (questSlots[i] == slot && e.getClick() == ClickType.SHIFT_LEFT) {
                QuestTracker.toggle(player, QuestTracker.Mode.DAILY, storage, hard, delivery);
                return;
            }
        }

        int[] claimSlots = QuestGui.claimSlots();
        int questIndex = -1;
        for (int i = 0; i < claimSlots.length; i++) {
            if (claimSlots[i] == slot) { questIndex = i; break; }
        }
        if (questIndex < 0) return;

        PlayerQuestState state = storage.get(player.getUniqueId());
        if (questIndex >= state.quests.size()) return;
        ActiveQuest q = state.quests.get(questIndex);

        if (!q.isClaimable()) return;

        q.rewardClaimed = true;
        storage.save(player.getUniqueId());

        if (economy != null) economy.depositPlayer(player, q.moneyReward);
        player.giveExp(q.expReward);
        player.sendMessage("§a§l✦ §aНаграда получена! §6+" + (int) q.moneyReward + " монет §7+ §b" + q.expReward + " опыта");

        // Award Battle Pass XP for completing a daily quest
        VoidRpDailyQuestsPlugin.fireBattlePassHook("onDailyQuestClaim", player);

        // Refresh GUI + tracker
        player.openInventory(QuestGui.build(state.quests));
        QuestTracker.refresh(player, storage, hard, delivery);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void trackProgress(Player player, QuestType type, String target, int amount) {
        PlayerQuestState state = storage.get(player.getUniqueId());
        boolean dirty = false;
        for (ActiveQuest q : state.quests) {
            if (q.type != type) continue;
            if (q.isCompleted()) continue;
            if (!q.target.equalsIgnoreCase(target)) continue;
            if (awardProgress(player, q, amount)) dirty = true;
        }
        if (dirty) storage.save(player.getUniqueId());
    }

    /** Returns true if progress was actually changed */
    private boolean awardProgress(Player player, ActiveQuest q, int amount) {
        int added = q.addProgress(amount);
        if (added <= 0) return false;

        if (q.isCompleted()) {
            player.sendMessage(color(questCompleteMsg.replace("{quest}", q.displayName)));
        }
        return true;
    }

    private static String color(String s) {
        return s.replace("&", "§");
    }
}
