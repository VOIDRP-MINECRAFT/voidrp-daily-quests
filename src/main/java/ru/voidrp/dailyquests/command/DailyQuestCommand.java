package ru.voidrp.dailyquests.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.voidrp.dailyquests.gui.QuestGui;
import ru.voidrp.dailyquests.player.DeliveryQuestStorage;
import ru.voidrp.dailyquests.player.HardQuestStorage;
import ru.voidrp.dailyquests.player.PlayerQuestState;
import ru.voidrp.dailyquests.player.QuestStorage;

public final class DailyQuestCommand implements CommandExecutor {

    private final QuestStorage         storage;
    private final HardQuestStorage     hard;
    private final DeliveryQuestStorage delivery;

    public DailyQuestCommand(QuestStorage storage, HardQuestStorage hard, DeliveryQuestStorage delivery) {
        this.storage  = storage;
        this.hard     = hard;
        this.delivery = delivery;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("dqadmin")) {
            return handleAdmin(sender, args);
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cТолько для игроков.");
            return true;
        }

        PlayerQuestState state = storage.get(player.getUniqueId());
        player.openInventory(QuestGui.build(state.quests));
        return true;
    }

    private boolean handleAdmin(CommandSender sender, String[] args) {
        if (!sender.hasPermission("voidrp.dailyquests.admin")) {
            sender.sendMessage("§cНет прав.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage("§eИспользование: /dqadmin reload | reset <player> | info <player>");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "reload" -> {
                storage.reload();
                hard.reload();
                delivery.reload();
                // re-load online players
                for (Player p : Bukkit.getOnlinePlayers()) {
                    storage.ensureToday(p.getUniqueId());
                    hard.ensureCurrentPeriod(p.getUniqueId());
                    delivery.ensureCurrentPeriod(p.getUniqueId());
                }
                sender.sendMessage("§aПлагин квестов перезагружен — данные сброшены из памяти и перечитаны с диска.");
            }
            case "reset" -> {
                if (args.length < 2) { sender.sendMessage("§e/dqadmin reset <player>"); return true; }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) { sender.sendMessage("§cИгрок не в сети."); return true; }
                PlayerQuestState state = storage.get(target.getUniqueId());
                state.lastResetDate = ""; // force regeneration on next ensureToday
                state.quests.clear();
                storage.save(target.getUniqueId());
                boolean fresh = storage.ensureToday(target.getUniqueId());
                sender.sendMessage("§aКвесты для §f" + target.getName() + " §aсброшены и обновлены.");
            }
            case "info" -> {
                if (args.length < 2) { sender.sendMessage("§e/dqadmin info <player>"); return true; }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) { sender.sendMessage("§cИгрок не в сети."); return true; }
                PlayerQuestState state = storage.get(target.getUniqueId());
                sender.sendMessage("§6Квесты §f" + target.getName() + " §6(дата: " + state.lastResetDate + "):");
                for (var q : state.quests) {
                    String status = q.rewardClaimed ? "§7✔" : q.isCompleted() ? "§a✔" : "§e○";
                    sender.sendMessage("  " + status + " §f" + q.displayName + " §7(" + q.progress + "/" + q.required + ")");
                }
            }
            default -> sender.sendMessage("§eНеизвестная команда. Используй: reload | reset | info");
        }
        return true;
    }
}
