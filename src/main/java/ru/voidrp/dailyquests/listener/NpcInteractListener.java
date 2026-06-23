package ru.voidrp.dailyquests.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import ru.voidrp.dailyquests.player.PlayerQuestState;
import ru.voidrp.dailyquests.player.QuestStorage;
import ru.voidrp.dailyquests.gui.QuestGui;

import java.util.List;

/**
 * Opens the quest GUI when a player right-clicks an entity whose custom name
 * matches one of the configured NPC names.
 *
 * If you use CitizensCMD, this listener is not needed — just bind /dailyquest
 * to the NPC via: /npc command add right -p /dailyquest
 */
public final class NpcInteractListener implements Listener {

    private final QuestStorage storage;
    private final List<String> npcNames;

    public NpcInteractListener(QuestStorage storage, List<String> npcNames) {
        this.storage  = storage;
        this.npcNames = npcNames;
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        Entity entity = e.getRightClicked();
        String name = entity.getCustomName();
        if (name == null) return;

        String nameStripped = ChatColor.stripColor(name);
        boolean match = false;
        for (String n : npcNames) {
            if (ChatColor.stripColor(n.replace("&", "§")).equalsIgnoreCase(nameStripped)) {
                match = true; break;
            }
        }
        if (!match) return;

        e.setCancelled(true);
        Player player = e.getPlayer();
        PlayerQuestState state = storage.get(player.getUniqueId());
        player.openInventory(QuestGui.build(state.quests));
    }
}
