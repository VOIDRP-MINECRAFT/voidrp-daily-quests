package ru.voidrp.dailyquests.tracker;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import ru.voidrp.dailyquests.player.DeliveryQuestStorage;
import ru.voidrp.dailyquests.player.HardQuestStorage;
import ru.voidrp.dailyquests.player.PlayerQuestState;
import ru.voidrp.dailyquests.player.QuestStorage;
import ru.voidrp.dailyquests.quest.ActiveQuest;
import ru.voidrp.dailyquests.quest.DeliveryActiveQuest;
import ru.voidrp.dailyquests.quest.RequiredItem;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Renders a sidebar scoreboard showing pinned quest progress.
 * Players toggle it with Shift+Click in the GUI or /questtrack.
 *
 * Supports 3 modes — whichever quest the player pinned last:
 *   DAILY    — up to 3 daily quests
 *   HARD     — 1 hero challenge quest
 *   DELIVERY — 1 delivery quest (shows item counts)
 */
public final class QuestTracker {

    public enum Mode { DAILY, HARD, DELIVERY }

    private static final String OBJ_NAME = "voidrp_qt";

    // Players who have a pinned tracker
    private static final ConcurrentHashMap<UUID, Mode> pinned = new ConcurrentHashMap<>();

    private QuestTracker() {}

    public static boolean isPinned(UUID uuid) { return pinned.containsKey(uuid); }
    public static Mode    getMode(UUID uuid)  { return pinned.get(uuid); }

    public static void pin(Player player, Mode mode,
                           QuestStorage daily, HardQuestStorage hard,
                           DeliveryQuestStorage delivery) {
        pinned.put(player.getUniqueId(), mode);
        refresh(player, daily, hard, delivery);
        player.sendMessage("§a§l✦ §aТрекер квестов закреплён справа. §7/questtrack — убрать");
    }

    public static void unpin(Player player) {
        pinned.remove(player.getUniqueId());
        removeScoreboard(player);
        player.sendMessage("§7Трекер квестов откреплён.");
    }

    public static void toggle(Player player, Mode mode,
                               QuestStorage daily, HardQuestStorage hard,
                               DeliveryQuestStorage delivery) {
        Mode current = pinned.get(player.getUniqueId());
        if (current == mode) {
            unpin(player);
        } else {
            pin(player, mode, daily, hard, delivery);
        }
    }

    public static void refresh(Player player,
                                QuestStorage daily, HardQuestStorage hard,
                                DeliveryQuestStorage delivery) {
        Mode mode = pinned.get(player.getUniqueId());
        if (mode == null) return;

        switch (mode) {
            case DAILY    -> renderDaily(player, daily);
            case HARD     -> renderHard(player, hard);
            case DELIVERY -> renderDelivery(player, delivery);
        }
    }

    public static void onQuit(UUID uuid) { pinned.remove(uuid); }

    // ── Renderers ─────────────────────────────────────────────────────────────

    @SuppressWarnings("deprecation")
    private static void renderDaily(Player player, QuestStorage daily) {
        PlayerQuestState state = daily.get(player.getUniqueId());
        ScoreboardManager mgr = Bukkit.getScoreboardManager();
        Scoreboard sb = mgr.getNewScoreboard();

        Objective obj = sb.registerNewObjective(OBJ_NAME, Criteria.DUMMY,
            Component.text("Ежедневные квесты", NamedTextColor.GOLD, TextDecoration.BOLD));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        int line = 15;
        line = addLine(obj, sb, "§8────────────", line);
        for (ActiveQuest q : state.quests) {
            String status = q.rewardClaimed ? "§7✔" : q.isCompleted() ? "§a✔" : "§e○";
            String name   = q.displayName.length() > 18 ? q.displayName.substring(0, 18) : q.displayName;
            line = addLine(obj, sb, status + " §f" + name, line);
            line = addLine(obj, sb, "  " + q.progressBar() + " §e" + q.progress + "§7/" + q.required, line);
        }
        line = addLine(obj, sb, "§8────────────", line);
        line = addLine(obj, sb, "§7/dq §8• §7Shift+Click §8=", line);
        line = addLine(obj, sb, "§7открепить", line);

        player.setScoreboard(sb);
    }

    @SuppressWarnings("deprecation")
    private static void renderHard(Player player, HardQuestStorage hard) {
        PlayerQuestState state = hard.get(player.getUniqueId());
        ScoreboardManager mgr = Bukkit.getScoreboardManager();
        Scoreboard sb = mgr.getNewScoreboard();

        Objective obj = sb.registerNewObjective(OBJ_NAME, Criteria.DUMMY,
            Component.text("⚔ Испытание Героя", NamedTextColor.RED, TextDecoration.BOLD));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        int line = 15;
        line = addLine(obj, sb, "§8────────────", line);
        if (state.quests.isEmpty()) {
            line = addLine(obj, sb, "§7Нет активного испытания", line);
        } else {
            ActiveQuest q = state.quests.get(0);
            String name = q.displayName.length() > 18 ? q.displayName.substring(0, 18) : q.displayName;
            line = addLine(obj, sb, (q.isCompleted() ? "§a✔ " : "§c☠ ") + "§f" + name, line);
            line = addLine(obj, sb, "  " + q.progressBar() + " §e" + q.progress + "§7/" + q.required, line);
            line = addLine(obj, sb, "§7До сброса: §f" + HardQuestStorage.daysUntilReset() + " дн.", line);
        }
        line = addLine(obj, sb, "§8────────────", line);
        line = addLine(obj, sb, "§7/bq §8• §7Shift+Click §8=", line);
        line = addLine(obj, sb, "§7открепить", line);

        player.setScoreboard(sb);
    }

    @SuppressWarnings("deprecation")
    private static void renderDelivery(Player player, DeliveryQuestStorage delivery) {
        DeliveryActiveQuest quest = delivery.get(player.getUniqueId());
        ScoreboardManager mgr = Bukkit.getScoreboardManager();
        Scoreboard sb = mgr.getNewScoreboard();

        Objective obj = sb.registerNewObjective(OBJ_NAME, Criteria.DUMMY,
            Component.text("📦 Торговец", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        int line = 15;
        line = addLine(obj, sb, "§8────────────", line);
        if (quest == null) {
            line = addLine(obj, sb, "§7Нет активного задания", line);
        } else if (quest.isCompleted()) {
            line = addLine(obj, sb, "§a✔ Задание выполнено!", line);
            line = addLine(obj, sb, "§7До сброса: §f" + DeliveryQuestStorage.daysUntilReset() + " дн.", line);
        } else {
            String name = quest.displayName.length() > 16 ? quest.displayName.substring(0, 16) : quest.displayName;
            line = addLine(obj, sb, "§d" + name, line);
            line = addLine(obj, sb, "§8──────", line);
            for (RequiredItem req : quest.required) {
                int have = countInInventory(player, req.material);
                boolean ok = have >= req.amount;
                String label = req.displayName.length() > 12 ? req.displayName.substring(0, 12) : req.displayName;
                line = addLine(obj, sb, (ok ? "§a✔" : "§c○") + " §7" + label, line);
                line = addLine(obj, sb, "  " + (ok ? "§a" : "§e") + have + "§7/" + req.amount, line);
                if (line <= 1) break;
            }
        }
        line = addLine(obj, sb, "§8────────────", line);
        line = addLine(obj, sb, "§7/del §8• §7Shift+Click §8=", line);
        line = addLine(obj, sb, "§7открепить", line);

        player.setScoreboard(sb);
    }

    // ── Scoreboard helpers ────────────────────────────────────────────────────

    @SuppressWarnings("deprecation")
    private static int addLine(Objective obj, Scoreboard sb, String text, int score) {
        // Each entry must be unique — pad with invisible color codes if needed
        String entry = text;
        while (sb.getEntries().contains(entry)) entry = entry + "§r";
        Score s = obj.getScore(entry);
        s.setScore(score);
        return score - 1;
    }

    private static void removeScoreboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    private static int countInInventory(Player player, String materialName) {
        try {
            var mat = org.bukkit.Material.valueOf(materialName.toUpperCase());
            int count = 0;
            for (var s : player.getInventory().getContents()) {
                if (s != null && s.getType() == mat) count += s.getAmount();
            }
            return count;
        } catch (Exception e) { return 0; }
    }
}
