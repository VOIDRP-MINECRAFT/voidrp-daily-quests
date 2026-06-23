package ru.voidrp.dailyquests.listener;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import ru.voidrp.dailyquests.player.DeliveryQuestStorage;
import ru.voidrp.dailyquests.player.HardQuestStorage;
import ru.voidrp.dailyquests.player.PlayerQuestState;
import ru.voidrp.dailyquests.player.QuestStorage;
import ru.voidrp.dailyquests.quest.ActiveQuest;
import ru.voidrp.dailyquests.quest.QuestType;
import ru.voidrp.dailyquests.tracker.QuestTracker;
import ru.voidrp.gamesync.event.PlayerMarketTradeEvent;
import ru.voidrp.modsell.event.PlayerModSellEvent;

import java.util.logging.Logger;

/**
 * Tracks MARKET_SELL, MARKET_BUY, and MOD_SELL quest progress.
 * Registered only when VoidRpGameSync / VoidRpModSell are available.
 */
public final class MarketQuestProgressListener implements Listener {

    private final QuestStorage         storage;
    private final HardQuestStorage     hard;
    private final DeliveryQuestStorage delivery;
    private final Economy economy;
    private final String questCompleteMsg;
    private final Logger log;

    public MarketQuestProgressListener(QuestStorage storage, HardQuestStorage hard,
                                        DeliveryQuestStorage delivery,
                                        Economy economy, String questCompleteMsg, Logger log) {
        this.storage          = storage;
        this.hard             = hard;
        this.delivery         = delivery;
        this.economy          = economy;
        this.questCompleteMsg = questCompleteMsg;
        this.log              = log;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMarketTrade(PlayerMarketTradeEvent e) {
        org.bukkit.entity.Player player = org.bukkit.Bukkit.getPlayerExact(e.getPlayerName());
        if (player == null || !player.isOnline()) return;

        QuestType type = e.getRole() == PlayerMarketTradeEvent.Role.SELLER
                ? QuestType.MARKET_SELL
                : QuestType.MARKET_BUY;

        trackProgress(player, type, e.getItemKey(), e.getAmount());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onModSell(PlayerModSellEvent e) {
        org.bukkit.entity.Player player = org.bukkit.Bukkit.getPlayerExact(e.getPlayerName());
        if (player == null || !player.isOnline()) return;
        trackProgress(player, QuestType.MOD_SELL, e.getModId(), e.getAmount());
    }

    private void trackProgress(org.bukkit.entity.Player player, QuestType type, String target, int amount) {
        PlayerQuestState state = storage.get(player.getUniqueId());
        boolean dirty = false;
        for (ActiveQuest q : state.quests) {
            if (q.type != type || q.isCompleted()) continue;
            if (!q.target.equalsIgnoreCase("any") && !q.target.equalsIgnoreCase(target)) continue;
            int added = q.addProgress(amount);
            if (added > 0) {
                dirty = true;
                if (q.isCompleted()) {
                    player.sendMessage(color(questCompleteMsg.replace("{quest}", q.displayName)));
                }
            }
        }
        if (dirty) {
            storage.save(player.getUniqueId());
            QuestTracker.refresh(player, storage, hard, delivery);
        }
    }

    private static String color(String s) { return s.replace("&", "§"); }
}
