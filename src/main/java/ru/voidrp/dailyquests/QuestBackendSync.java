package ru.voidrp.dailyquests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import ru.voidrp.dailyquests.player.PlayerQuestState;
import ru.voidrp.dailyquests.player.QuestStorage;
import ru.voidrp.dailyquests.quest.ActiveQuest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Pushes a player's daily-quest snapshot to the backend (via GameSync's
 * BackendClient, by reflection) so the WebGUI can render it. Best-effort: any
 * failure is swallowed. All network work runs off the main thread.
 */
public final class QuestBackendSync {

    private static final Gson GSON = new GsonBuilder().create();

    private QuestBackendSync() {}

    public static void push(JavaPlugin plugin, QuestStorage storage, Player player) {
        if (plugin == null || player == null) return;
        if (!plugin.getConfig().getBoolean("webgui.enabled", false)) return;

        PlayerQuestState state = storage.get(player.getUniqueId());
        List<Map<String, Object>> daily = new ArrayList<>();
        int claimed = 0;
        int idx = 0;
        for (ActiveQuest q : state.quests) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("index", idx++);
            m.put("template_id", q.templateId);
            m.put("display_name", q.displayName);
            m.put("description", q.description);
            m.put("type", q.type != null ? q.type.name() : null);
            m.put("target", q.target);
            m.put("required", q.required);
            m.put("progress", Math.min(q.progress, q.required));
            m.put("money_reward", q.moneyReward);
            m.put("exp_reward", q.expReward);
            m.put("claimed", q.rewardClaimed);
            daily.add(m);
            if (q.rewardClaimed) claimed++;
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("minecraft_nickname", player.getName());
        payload.put("daily", daily);
        payload.put("completed_total", claimed);
        payload.put("reset_date", state.lastResetDate);
        String json = GSON.toJson(payload);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Plugin gs = Bukkit.getPluginManager().getPlugin("VoidRpGameSync");
                if (gs == null || !gs.isEnabled()) return;
                Object backend = gs.getClass().getMethod("getBackendClient").invoke(gs);
                if (backend == null) return;
                backend.getClass().getMethod("pushDailyQuestSnapshot", String.class).invoke(backend, json);
            } catch (Exception ignored) {
                // best-effort — WebGUI just shows the last known snapshot
            }
        });
    }
}
