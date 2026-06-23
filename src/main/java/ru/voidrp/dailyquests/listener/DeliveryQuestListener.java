package ru.voidrp.dailyquests.listener;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.voidrp.dailyquests.VoidRpDailyQuestsPlugin;
import ru.voidrp.dailyquests.gui.DeliveryQuestGui;
import ru.voidrp.dailyquests.player.DeliveryQuestStorage;
import ru.voidrp.dailyquests.quest.DeliveryActiveQuest;

import java.util.List;
import java.util.logging.Logger;

public final class DeliveryQuestListener implements Listener {

    private final DeliveryQuestStorage storage;
    private final Economy economy;
    private final List<String> npcNames;
    private final Logger log;

    private static final String NEW_QUEST_MSG =
        "§5§l📦 §dНовое задание Торговца Артефактами! §5§l📦 §7Используй /delivery";

    public DeliveryQuestListener(DeliveryQuestStorage storage, Economy economy,
                                  List<String> npcNames, Logger log) {
        this.storage  = storage;
        this.economy  = economy;
        this.npcNames = npcNames;
        this.log      = log;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        boolean fresh = storage.ensureCurrentPeriod(e.getPlayer().getUniqueId());
        if (fresh) e.getPlayer().sendMessage(NEW_QUEST_MSG);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        storage.evict(e.getPlayer().getUniqueId());
    }

    // ── NPC right-click ───────────────────────────────────────────────────────

    @EventHandler
    public void onNpcInteract(PlayerInteractEntityEvent e) {
        String name = e.getRightClicked().getCustomName();
        if (name == null) return;
        boolean match = npcNames.stream().anyMatch(n -> n.equals(name));
        if (!match) return;

        e.setCancelled(true);
        openGui(e.getPlayer());
    }

    // ── GUI click ─────────────────────────────────────────────────────────────

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!DeliveryQuestGui.TITLE.equals(e.getView().getTitle())) return;

        e.setCancelled(true);

        if (e.getRawSlot() != DeliveryQuestGui.SUBMIT_SLOT) return;

        DeliveryActiveQuest quest = storage.get(player.getUniqueId());
        if (quest == null || quest.isCompleted()) return;
        if (!DeliveryQuestGui.canSubmit(player, quest)) {
            player.sendMessage("§c✘ У вас не хватает предметов для сдачи!");
            return;
        }

        // Take items and give reward
        DeliveryQuestGui.takeItems(player, quest);
        quest.rewardClaimed = true;
        storage.save(player.getUniqueId());

        if (economy != null) economy.depositPlayer(player, quest.moneyReward);
        player.giveExp(quest.expReward);
        player.sendMessage("§5§l📦 §aЗадание выполнено: §f" + quest.displayName);
        player.sendMessage("§a§l    +" + (long) quest.moneyReward + " монет  §b+" + quest.expReward + " опыта");

        // Award Battle Pass XP for completing a delivery quest
        VoidRpDailyQuestsPlugin.fireBattlePassHook("onDeliveryQuestClaim", player);

        // Refresh GUI
        player.openInventory(DeliveryQuestGui.build(player, quest));
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    public void openGui(Player player) {
        storage.ensureCurrentPeriod(player.getUniqueId());
        DeliveryActiveQuest quest = storage.get(player.getUniqueId());
        player.openInventory(DeliveryQuestGui.build(player, quest));
    }
}
