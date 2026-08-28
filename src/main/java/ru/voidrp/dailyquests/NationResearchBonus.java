package ru.voidrp.dailyquests;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import ru.voidrp.gamesync.VoidRpGameSyncPlugin;
import ru.voidrp.gamesync.model.NationDefinition;

/**
 * Resolves extra daily-quest slots granted by a player's nation research
 * ("Биржа труда" → {@code extra_daily_quest_slots}). Returns 0 whenever GameSync
 * is absent, the player has no nation, or anything goes wrong.
 */
public final class NationResearchBonus {

    private NationResearchBonus() {}

    public static int extraQuestSlots(Player player) {
        if (player == null) return 0;
        try {
            Plugin gs = Bukkit.getPluginManager().getPlugin("VoidRpGameSync");
            if (!(gs instanceof VoidRpGameSyncPlugin gameSync)) return 0;
            NationDefinition nation = gameSync.getNationRegistry().findByPlayer(player.getName());
            if (nation == null) return 0;
            double slots = gameSync.getNationResearchEffectService().getEffect(nation.slug(), "extra_daily_quest_slots");
            return slots <= 0 ? 0 : (int) Math.round(slots);
        } catch (Throwable ignored) {
            return 0;
        }
    }
}
