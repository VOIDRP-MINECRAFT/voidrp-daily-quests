package ru.voidrp.dailyquests.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.voidrp.dailyquests.player.DeliveryQuestStorage;
import ru.voidrp.dailyquests.player.HardQuestStorage;
import ru.voidrp.dailyquests.player.QuestStorage;
import ru.voidrp.dailyquests.tracker.QuestTracker;

public final class QuestTrackCommand implements CommandExecutor {

    private final QuestStorage         daily;
    private final HardQuestStorage     hard;
    private final DeliveryQuestStorage delivery;

    public QuestTrackCommand(QuestStorage daily, HardQuestStorage hard,
                              DeliveryQuestStorage delivery) {
        this.daily    = daily;
        this.hard     = hard;
        this.delivery = delivery;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cТолько для игроков.");
            return true;
        }
        if (QuestTracker.isPinned(player.getUniqueId())) {
            QuestTracker.unpin(player);
        } else {
            // Default: show daily quests
            QuestTracker.pin(player, QuestTracker.Mode.DAILY, daily, hard, delivery);
        }
        return true;
    }
}
