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
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.ClickType;
import ru.voidrp.dailyquests.VoidRpDailyQuestsPlugin;
import ru.voidrp.dailyquests.gui.HardQuestGui;
import ru.voidrp.dailyquests.player.DeliveryQuestStorage;
import ru.voidrp.dailyquests.player.HardQuestStorage;
import ru.voidrp.dailyquests.player.PlayerQuestState;
import ru.voidrp.dailyquests.player.QuestStorage;
import ru.voidrp.dailyquests.quest.ActiveQuest;
import ru.voidrp.dailyquests.quest.QuestType;
import ru.voidrp.dailyquests.tracker.QuestTracker;

import java.util.logging.Logger;

public final class HardQuestProgressListener implements Listener {

    private final HardQuestStorage     storage;
    private final QuestStorage         daily;
    private final DeliveryQuestStorage delivery;
    private final Economy economy;
    private final Logger log;

    private static final String NEW_QUEST_MSG =
        "§4§l⚔ §cНовое Испытание Героя доступно! §4§l⚔ §7Используй /bq";

    public HardQuestProgressListener(HardQuestStorage storage,
                                     QuestStorage daily,
                                     DeliveryQuestStorage delivery,
                                     Economy economy, Logger log) {
        this.storage  = storage;
        this.daily    = daily;
        this.delivery = delivery;
        this.economy  = economy;
        this.log      = log;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        boolean fresh = storage.ensureCurrentPeriod(e.getPlayer().getUniqueId());
        if (fresh) e.getPlayer().sendMessage(NEW_QUEST_MSG);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        storage.evict(e.getPlayer().getUniqueId());
    }

    // ── Kill ──────────────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKill(EntityDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        if (killer == null) {
            var dmg = e.getEntity().getLastDamageCause();
            if (dmg instanceof EntityDamageByEntityEvent byEntity) {
                Entity damager = byEntity.getDamager();
                if (damager instanceof Player p) killer = p;
                else if (damager instanceof Projectile proj && proj.getShooter() instanceof Player p) killer = p;
            }
        }
        if (killer == null) return;
        String key = e.getEntity().getType().getKey().toString();
        track(killer, QuestType.KILL, key, 1);
    }

    // ── Collect ───────────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPickup(PlayerPickupItemEvent e) {
        String mat = e.getItem().getItemStack().getType().name();
        track(e.getPlayer(), QuestType.COLLECT, mat, e.getItem().getItemStack().getAmount());
    }

    // ── Mine ──────────────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        track(e.getPlayer(), QuestType.MINE, e.getBlock().getType().name(), 1);
    }

    // ── Craft ─────────────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraft(CraftItemEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        ItemStack result = e.getRecipe().getResult();
        if (result.getType().isAir()) return;

        // Shift-click: count minimum ingredient stack in the matrix to estimate batch size.
        int amount;
        if (e.isShiftClick()) {
            int minIngredient = Integer.MAX_VALUE;
            for (ItemStack ing : e.getInventory().getMatrix()) {
                if (ing != null && !ing.getType().isAir()) {
                    minIngredient = Math.min(minIngredient, ing.getAmount());
                }
            }
            amount = (minIngredient == Integer.MAX_VALUE ? 1 : minIngredient) * result.getAmount();
        } else {
            amount = result.getAmount();
        }

        track(player, QuestType.CRAFT, result.getType().name(), amount);
    }

    // ── Fish ──────────────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFish(PlayerFishEvent e) {
        if (e.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (e.getCaught() == null) return;
        String mat = "ANY";
        if (e.getCaught() instanceof org.bukkit.entity.Item item) {
            mat = item.getItemStack().getType().name();
        }
        PlayerQuestState state = storage.get(e.getPlayer().getUniqueId());
        for (ActiveQuest q : state.quests) {
            if (q.type != QuestType.FISH) continue;
            if (!q.isCompleted() && (q.target.equals("ANY") || q.target.equals(mat))) {
                if (q.addProgress(1) > 0 && q.isCompleted()) notifyComplete(e.getPlayer(), q);
            }
        }
        storage.save(e.getPlayer().getUniqueId());
    }

    // ── Breed ─────────────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreed(EntityBreedEvent e) {
        if (!(e.getBreeder() instanceof Player player)) return;
        track(player, QuestType.BREED, e.getEntity().getType().getKey().toString(), 1);
    }

    // ── GUI: claim reward ─────────────────────────────────────────────────────

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!HardQuestGui.TITLE.equals(e.getView().getTitle())) return;
        e.setCancelled(true);

        // Shift+Click на слоте квеста — переключить трекер
        if (e.getRawSlot() == HardQuestGui.QUEST_SLOT && e.getClick() == ClickType.SHIFT_LEFT) {
            QuestTracker.toggle(player, QuestTracker.Mode.HARD, daily, storage, delivery);
            return;
        }

        if (e.getRawSlot() != HardQuestGui.CLAIM_SLOT) return;

        PlayerQuestState state = storage.get(player.getUniqueId());
        if (state.quests.isEmpty()) return;
        ActiveQuest q = state.quests.get(0);

        if (!q.isClaimable()) return;
        q.rewardClaimed = true;
        storage.save(player.getUniqueId());

        if (economy != null) economy.depositPlayer(player, q.moneyReward);
        player.giveExp(q.expReward);
        player.sendMessage("§4§l⚔ §aИспытание завершено! §6+" + (int) q.moneyReward + " монет §7+ §b" + q.expReward + " опыта");

        // Award Battle Pass XP for completing a boss quest
        VoidRpDailyQuestsPlugin.fireBattlePassHook("onBossQuestClaim", player);

        player.openInventory(HardQuestGui.build(state.quests));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void track(Player player, QuestType type, String target, int amount) {
        PlayerQuestState state = storage.get(player.getUniqueId());
        boolean dirty = false;
        for (ActiveQuest q : state.quests) {
            if (q.type != type || q.isCompleted()) continue;
            if (!q.target.equalsIgnoreCase(target)) continue;
            int added = q.addProgress(amount);
            if (added > 0) {
                dirty = true;
                if (q.isCompleted()) notifyComplete(player, q);
            }
        }
        if (dirty) storage.save(player.getUniqueId());
    }

    private static void notifyComplete(Player player, ActiveQuest q) {
        player.sendMessage("§4§l⚔ §aИспытание выполнено: §f" + q.displayName + " §a| Забери награду (/bq)");
    }
}
