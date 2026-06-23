package ru.voidrp.dailyquests.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.voidrp.dailyquests.quest.ActiveQuest;
import ru.voidrp.dailyquests.quest.QuestType;

import java.util.ArrayList;
import java.util.List;

public final class QuestGui {

    public static final String TITLE = "§6§lЕжедневные квесты";

    // Slots for quests in a 27-slot (3-row) chest
    private static final int[] QUEST_SLOTS = {10, 13, 16};
    private static final int[] CLAIM_SLOTS  = {19, 22, 25};

    private QuestGui() {}

    public static Inventory build(List<ActiveQuest> quests) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        // Border with gray glass
        ItemStack border = glass(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; i++) inv.setItem(i, border);

        for (int i = 0; i < Math.min(quests.size(), QUEST_SLOTS.length); i++) {
            ActiveQuest q = quests.get(i);
            inv.setItem(QUEST_SLOTS[i], questItem(q));
            inv.setItem(CLAIM_SLOTS[i], claimItem(q));
        }
        return inv;
    }

    private static ItemStack questItem(ActiveQuest q) {
        Material mat = iconFor(q.type);
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        String statusPrefix;
        if (q.rewardClaimed)       statusPrefix = "§7§m";
        else if (q.isCompleted())  statusPrefix = "§a§l✔ ";
        else                       statusPrefix = "§e";

        meta.setDisplayName(statusPrefix + q.displayName);

        List<String> lore = new ArrayList<>();
        lore.add("§8§m──────────────────");
        lore.add("§7" + q.description.replace("{n}", String.valueOf(q.required)));
        lore.add("");
        lore.add("§7Прогресс: " + q.progressBar() + " §e" + q.progress + "§7/§f" + q.required);
        lore.add("");
        lore.add("§7Награда: §6" + (int) q.moneyReward + " монет §7+ §b" + q.expReward + " опыта");
        lore.add("§8§m──────────────────");
        lore.add("§8Shift+ЛКМ — закрепить на экране");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack claimItem(ActiveQuest q) {
        if (q.rewardClaimed) {
            return glass(Material.RED_STAINED_GLASS_PANE, "§7Награда получена");
        } else if (q.isCompleted()) {
            ItemStack item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§a§l» Забрать награду «");
            List<String> lore = new ArrayList<>();
            lore.add("§6" + (int) q.moneyReward + " монет");
            lore.add("§b" + q.expReward + " опыта");
            meta.setLore(lore);
            item.setItemMeta(meta);
            return item;
        } else {
            return glass(Material.YELLOW_STAINED_GLASS_PANE, "§eВ процессе...");
        }
    }

    private static ItemStack glass(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    private static Material iconFor(QuestType type) {
        if (type == null) return Material.PAPER;
        return switch (type) {
            case KILL        -> Material.IRON_SWORD;
            case COLLECT     -> Material.CHEST;
            case MINE        -> Material.IRON_PICKAXE;
            case FISH        -> Material.FISHING_ROD;
            case BREED       -> Material.WHEAT;
            case CRAFT       -> Material.CRAFTING_TABLE;
            case MARKET_SELL -> Material.GOLD_INGOT;
            case MARKET_BUY  -> Material.EMERALD;
            case MOD_SELL    -> Material.NETHER_STAR;
        };
    }

    public static int[] claimSlots() { return CLAIM_SLOTS; }
}
