package ru.voidrp.dailyquests.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.voidrp.dailyquests.gui.HardQuestGui;
import ru.voidrp.dailyquests.player.HardQuestStorage;
import ru.voidrp.dailyquests.player.PlayerQuestState;

public final class BossQuestCommand implements CommandExecutor {

    private final HardQuestStorage storage;

    public BossQuestCommand(HardQuestStorage storage) {
        this.storage = storage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("bqadmin")) {
            return handleAdmin(sender, args);
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cТолько для игроков.");
            return true;
        }

        PlayerQuestState state = storage.get(player.getUniqueId());
        player.openInventory(HardQuestGui.build(state.quests));
        return true;
    }

    private boolean handleAdmin(CommandSender sender, String[] args) {
        if (!sender.hasPermission("voidrp.dailyquests.admin")) {
            sender.sendMessage("§cНет прав.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage("§e/bqadmin <reset <player>|info <player>>");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "reset" -> {
                if (args.length < 2) { sender.sendMessage("§e/bqadmin reset <player>"); return true; }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) { sender.sendMessage("§cИгрок не в сети."); return true; }
                PlayerQuestState state = storage.get(target.getUniqueId());
                state.lastResetDate = "";
                state.quests.clear();
                storage.save(target.getUniqueId());
                storage.ensureCurrentPeriod(target.getUniqueId());
                sender.sendMessage("§aИспытание для §f" + target.getName() + " §aсброшено.");
            }
            case "info" -> {
                if (args.length < 2) { sender.sendMessage("§e/bqadmin info <player>"); return true; }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) { sender.sendMessage("§cИгрок не в сети."); return true; }
                PlayerQuestState state = storage.get(target.getUniqueId());
                sender.sendMessage("§cИспытание §f" + target.getName() + "§c:");
                for (var q : state.quests) {
                    String status = q.rewardClaimed ? "§7✔" : q.isCompleted() ? "§a✔" : "§e○";
                    sender.sendMessage("  " + status + " §f" + q.displayName + " §7(" + q.progress + "/" + q.required + ")");
                }
                sender.sendMessage("  §7До сброса: §f" + HardQuestStorage.daysUntilReset() + " дн.");
            }
            default -> sender.sendMessage("§eНеизвестная команда.");
        }
        return true;
    }
}
