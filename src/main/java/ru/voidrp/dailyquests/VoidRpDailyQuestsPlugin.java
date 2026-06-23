package ru.voidrp.dailyquests;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.voidrp.dailyquests.command.BossQuestCommand;
import ru.voidrp.dailyquests.command.DailyQuestCommand;
import ru.voidrp.dailyquests.command.QuestTrackCommand;
import ru.voidrp.dailyquests.listener.DeliveryQuestListener;
import ru.voidrp.dailyquests.listener.HardQuestProgressListener;
import ru.voidrp.dailyquests.listener.MarketQuestProgressListener;
import ru.voidrp.dailyquests.listener.NpcInteractListener;
import ru.voidrp.dailyquests.listener.QuestProgressListener;
import ru.voidrp.dailyquests.player.DeliveryQuestStorage;
import ru.voidrp.dailyquests.player.HardQuestStorage;
import ru.voidrp.dailyquests.player.QuestStorage;
import ru.voidrp.dailyquests.tracker.QuestTracker;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public final class VoidRpDailyQuestsPlugin extends JavaPlugin {

    private QuestStorage         dailyStorage;
    private HardQuestStorage     hardStorage;
    private DeliveryQuestStorage deliveryStorage;
    private Economy economy;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        Path base = getDataFolder().toPath();
        int questsPerDay = getConfig().getInt("quests-per-day", 3);
        dailyStorage    = new QuestStorage(base.resolve("data"),     questsPerDay, getLogger());
        hardStorage     = new HardQuestStorage(base.resolve("hard"),              getLogger());
        deliveryStorage = new DeliveryQuestStorage(base.resolve("delivery"),      getLogger());

        economy = setupEconomy();
        if (economy == null) getLogger().warning("Vault not found — money rewards disabled.");

        String newQuestsMsg     = getConfig().getString("new-quests-message",     "&6Новые ежедневные квесты доступны! Используй /dq");
        String questCompleteMsg = getConfig().getString("quest-complete-message",  "&aКвест выполнен: {quest}");
        List<String> npcNames   = getConfig().getStringList("npc-names");
        List<String> delNpcNames = getConfig().getStringList("delivery-npc-names");

        // ── Listeners ──
        getServer().getPluginManager().registerEvents(
            new QuestProgressListener(dailyStorage, hardStorage, deliveryStorage,
                                      economy, newQuestsMsg, questCompleteMsg, getLogger()), this);
        getServer().getPluginManager().registerEvents(
            new HardQuestProgressListener(hardStorage, dailyStorage, deliveryStorage, economy, getLogger()), this);

        DeliveryQuestListener deliveryListener =
            new DeliveryQuestListener(deliveryStorage, economy, delNpcNames, getLogger());
        getServer().getPluginManager().registerEvents(deliveryListener, this);
        getServer().getPluginManager().registerEvents(
            new NpcInteractListener(dailyStorage, npcNames), this);

        // Market + ModSell quest listener (registered unconditionally;
        // events simply won't fire if the source plugins aren't loaded)
        getServer().getPluginManager().registerEvents(
            new MarketQuestProgressListener(dailyStorage, hardStorage, deliveryStorage,
                                             economy, questCompleteMsg, getLogger()), this);

        // ── Commands ──
        DailyQuestCommand dqCmd = new DailyQuestCommand(dailyStorage, hardStorage, deliveryStorage);
        getCommand("dailyquest").setExecutor(dqCmd);
        getCommand("dqadmin").setExecutor(dqCmd);

        BossQuestCommand bqCmd = new BossQuestCommand(hardStorage);
        getCommand("bossquest").setExecutor(bqCmd);
        getCommand("bqadmin").setExecutor(bqCmd);

        getCommand("delivery").setExecutor((sender, cmd, label, args) -> {
            if (!(sender instanceof Player p)) { sender.sendMessage("§cТолько для игроков."); return true; }
            deliveryListener.openGui(p);
            return true;
        });

        getCommand("questtrack").setExecutor(
            new QuestTrackCommand(dailyStorage, hardStorage, deliveryStorage));

        // ── Load quests for already-online players ──
        for (Player p : Bukkit.getOnlinePlayers()) {
            dailyStorage.ensureToday(p.getUniqueId());
            hardStorage.ensureCurrentPeriod(p.getUniqueId());
            deliveryStorage.ensureCurrentPeriod(p.getUniqueId());
        }

        // ── Midnight check + tracker refresh (every minute) ──
        int resetHour = getConfig().getInt("reset-hour", 0);
        new BukkitRunnable() {
            private int lastCheckedHour = -1;
            @Override public void run() {
                int h = LocalDateTime.now().getHour();
                if (h == resetHour && lastCheckedHour != resetHour) {
                    lastCheckedHour = h;
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (dailyStorage.ensureToday(p.getUniqueId()))
                            p.sendMessage(color(newQuestsMsg));
                        if (hardStorage.ensureCurrentPeriod(p.getUniqueId()))
                            p.sendMessage("§4§l⚔ §cНовое Испытание Героя! Используй /bq");
                        if (deliveryStorage.ensureCurrentPeriod(p.getUniqueId()))
                            p.sendMessage("§5§l📦 §dНовое задание Торговца! Используй /delivery");
                        QuestTracker.refresh(p, dailyStorage, hardStorage, deliveryStorage);
                    }
                } else if (h != resetHour) {
                    lastCheckedHour = -1;
                }
                // Refresh trackers every minute for live delivery item counts
                for (Player p : Bukkit.getOnlinePlayers()) {
                    QuestTracker.refresh(p, dailyStorage, hardStorage, deliveryStorage);
                }
            }
        }.runTaskTimer(this, 1200L, 1200L);

        // ── Auto-save every 5 minutes ──
        new BukkitRunnable() {
            @Override public void run() {
                dailyStorage.saveAll();
                hardStorage.saveAll();
                deliveryStorage.saveAll();
            }
        }.runTaskTimerAsynchronously(this, 6000L, 6000L);

        getLogger().info("VoidRp Daily Quests enabled — daily:" + questsPerDay + " | hard:1/3d | delivery:1/7d");
    }

    @Override
    public void onDisable() {
        if (dailyStorage    != null) dailyStorage.saveAll();
        if (hardStorage     != null) hardStorage.saveAll();
        if (deliveryStorage != null) deliveryStorage.saveAll();
        for (Player p : Bukkit.getOnlinePlayers()) QuestTracker.onQuit(p.getUniqueId());
        getLogger().info("VoidRp Daily Quests disabled — all data saved");
    }

    private Economy setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return null;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        return rsp == null ? null : rsp.getProvider();
    }

    /**
     * Calls a named static Consumer<Player> field in BattlePassHooks via reflection.
     * No-op if BattlePass is not loaded or the hook is null.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void fireBattlePassHook(String hookName, Player player) {
        try {
            Class<?> cls = Class.forName("ru.voidrp.battlepass.BattlePassHooks");
            java.lang.reflect.Field f = cls.getDeclaredField(hookName);
            java.util.function.Consumer hook = (java.util.function.Consumer) f.get(null);
            if (hook != null) hook.accept(player);
        } catch (Exception ignored) {}
    }

    private static String color(String s) { return s.replace("&", "§"); }
}
