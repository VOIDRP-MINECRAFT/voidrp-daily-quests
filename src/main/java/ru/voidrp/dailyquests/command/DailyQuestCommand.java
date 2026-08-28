package ru.voidrp.dailyquests.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;
import ru.voidrp.dailyquests.QuestBackendSync;
import ru.voidrp.dailyquests.VoidRpDailyQuestsPlugin;
import ru.voidrp.dailyquests.gui.QuestGui;
import ru.voidrp.dailyquests.player.DeliveryQuestStorage;
import ru.voidrp.dailyquests.player.HardQuestStorage;
import ru.voidrp.dailyquests.player.PlayerQuestState;
import ru.voidrp.dailyquests.player.QuestStorage;
import ru.voidrp.dailyquests.quest.ActiveQuest;

public final class DailyQuestCommand implements CommandExecutor {

    private final QuestStorage         storage;
    private final HardQuestStorage     hard;
    private final DeliveryQuestStorage delivery;
    private JavaPlugin plugin;
    private Economy economy;

    public void setPlugin(JavaPlugin plugin) { this.plugin = plugin; }
    public void setEconomy(Economy economy) { this.economy = economy; }

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

        // /dailyquest claim <index> — used by the WebGUI (via a whitelisted web action)
        // to claim a completed daily quest without opening the chest GUI.
        if (args.length >= 2 && args[0].equalsIgnoreCase("claim")) {
            handleClaim(player, args[1]);
            return true;
        }

        if (!tryOpenWebGui(player)) {
            PlayerQuestState state = storage.get(player.getUniqueId());
            player.openInventory(QuestGui.build(state.quests));
        }
        return true;
    }

    private void handleClaim(Player player, String indexArg) {
        int index;
        try {
            index = Integer.parseInt(indexArg);
        } catch (NumberFormatException e) {
            player.sendMessage("§cНеверный номер квеста.");
            return;
        }
        PlayerQuestState state = storage.get(player.getUniqueId());
        if (index < 0 || index >= state.quests.size()) {
            player.sendMessage("§cКвест не найден.");
            return;
        }
        ActiveQuest q = state.quests.get(index);
        if (!q.isClaimable()) {
            player.sendMessage("§cЭтот квест ещё нельзя забрать.");
            return;
        }
        q.rewardClaimed = true;
        storage.save(player.getUniqueId());
        if (economy != null) economy.depositPlayer(player, q.moneyReward);
        player.giveExp(q.expReward);
        player.sendMessage("§a§l✦ §aНаграда получена! §6+" + (int) q.moneyReward + " монет §7+ §b" + q.expReward + " опыта");
        VoidRpDailyQuestsPlugin.fireBattlePassHook("onDailyQuestClaim", player);
        if (plugin != null) QuestBackendSync.push(plugin, storage, player);
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
                    storage.ensureToday(p.getUniqueId(), ru.voidrp.dailyquests.NationResearchBonus.extraQuestSlots(p));
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
                boolean fresh = storage.ensureToday(target.getUniqueId(), ru.voidrp.dailyquests.NationResearchBonus.extraQuestSlots(target));
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

    private boolean tryOpenWebGui(Player player) {
        if (plugin == null) return false;
        if (!plugin.getConfig().getBoolean("webgui.enabled", false)) return false;
        String url = plugin.getConfig().getString("webgui.quests-url", "https://void-rp.ru/game-ui/quests");
        try {
            org.bukkit.plugin.Plugin gsPlugin = Bukkit.getPluginManager().getPlugin("VoidRpGameSync");
            if (gsPlugin == null || !gsPlugin.isEnabled()) return false;
            Object bridgeService = gsPlugin.getClass().getMethod("getWebGuiBridgeService").invoke(gsPlugin);
            if (bridgeService == null) return false;
            bridgeService.getClass().getMethod("openGui", Player.class, String.class).invoke(bridgeService, player, url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
