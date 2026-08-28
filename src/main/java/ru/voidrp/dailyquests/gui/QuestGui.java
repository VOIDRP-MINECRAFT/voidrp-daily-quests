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

    // Max quests the 27-slot GUI supports (3 base + nation-research extra slots).
    public static final int MAX_SLOTS = 4;

    /** Quest icon slots for {@code count} quests, centred in the middle row. */
    public static int[] questSlots(int count) {
        return switch (Math.max(1, Math.min(count, MAX_SLOTS))) {
            case 1 -> new int[]{13};
            case 2 -> new int[]{11, 15};
            case 4 -> new int[]{10, 12, 14, 16};
            default -> new int[]{10, 13, 16};
        };
    }

    /** Claim button slots directly under each quest icon. */
    public static int[] claimSlots(int count) {
        int[] q = questSlots(count);
        int[] c = new int[q.length];
        for (int i = 0; i < q.length; i++) c[i] = q[i] + 9; // row directly below
        return c;
    }

    private QuestGui() {}

    public static Inventory build(List<ActiveQuest> quests) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        // Border with gray glass
        ItemStack border = glass(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; i++) inv.setItem(i, border);

        int shown = Math.min(quests.size(), MAX_SLOTS);
        int[] questSlots = questSlots(shown);
        int[] claimSlots = claimSlots(shown);
        for (int i = 0; i < shown; i++) {
            ActiveQuest q = quests.get(i);
            inv.setItem(questSlots[i], questItem(q));
            inv.setItem(claimSlots[i], claimItem(q));
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
}
