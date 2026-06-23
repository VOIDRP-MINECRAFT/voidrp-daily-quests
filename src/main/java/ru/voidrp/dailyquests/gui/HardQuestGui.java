package ru.voidrp.dailyquests.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.voidrp.dailyquests.player.HardQuestStorage;
import ru.voidrp.dailyquests.quest.ActiveQuest;
import ru.voidrp.dailyquests.quest.QuestType;

import java.util.ArrayList;
import java.util.List;

public final class HardQuestGui {

    public static final String TITLE  = "§4§l⚔ Испытание Героя ⚔";
    public static final int    QUEST_SLOT = 13;
    public static final int    CLAIM_SLOT = 22;

    private HardQuestGui() {}

    public static Inventory build(List<ActiveQuest> quests) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        // Decorative border — dark red glass
        ItemStack border = glass(Material.RED_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; i++) inv.setItem(i, border);

        if (quests.isEmpty()) {
            inv.setItem(QUEST_SLOT, noQuest());
            return inv;
        }

        ActiveQuest q = quests.get(0);
        inv.setItem(QUEST_SLOT,  questItem(q));
        inv.setItem(CLAIM_SLOT,  claimItem(q));

        // Timer display
        inv.setItem(4, timerItem());
        return inv;
    }

    private static ItemStack questItem(ActiveQuest q) {
        Material mat = iconFor(q.type);
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        String prefix;
        if (q.rewardClaimed)      prefix = "§8§m";
        else if (q.isCompleted()) prefix = "§a§l✔ ";
        else                      prefix = "§c§l☠ ";

        meta.setDisplayName(prefix + q.displayName);

        List<String> lore = new ArrayList<>();
        lore.add("§8§m─────────────────────");
        lore.add("§7" + q.description.replace("{n}", String.valueOf(q.required)));
        lore.add("");
        lore.add("§7Прогресс: " + q.progressBar() + " §e" + q.progress + "§7/§f" + q.required);
        lore.add("");
        lore.add("§6Награда:");
        lore.add("  §f" + (int) q.moneyReward + " §6монет");
        lore.add("  §b" + q.expReward + " §7очков опыта");
        lore.add("§8§m─────────────────────");
        if (!q.rewardClaimed && q.isCompleted()) {
            lore.add("§a§l» Нажмите CLAIM для награды «");
        }
        lore.add("§8Shift+ЛКМ — закрепить на экране");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack claimItem(ActiveQuest q) {
        if (q.rewardClaimed) {
            ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§7Награда уже получена");
            item.setItemMeta(meta);
            return item;
        } else if (q.isCompleted()) {
            ItemStack item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§a§l✦ ЗАБРАТЬ НАГРАДУ ✦");
            List<String> lore = new ArrayList<>();
            lore.add("§6+" + (int) q.moneyReward + " монет");
            lore.add("§b+" + q.expReward + " опыта");
            meta.setLore(lore);
            item.setItemMeta(meta);
            return item;
        } else {
            ItemStack item = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§eВыполняется...");
            item.setItemMeta(meta);
            return item;
        }
    }

    private static ItemStack timerItem() {
        ItemStack item = new ItemStack(Material.CLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§e§lДо обновления:");
        List<String> lore = new ArrayList<>();
        int days = HardQuestStorage.daysUntilReset();
        lore.add("§f" + days + " §7" + (days == 1 ? "день" : days < 5 ? "дня" : "дней"));
        lore.add("§8Новое испытание каждые 3 дня");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack noQuest() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§cИспытание недоступно");
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack glass(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    private static Material iconFor(QuestType type) {
        if (type == null) return Material.NETHER_STAR;
        return switch (type) {
            case KILL        -> Material.NETHERITE_SWORD;
            case COLLECT     -> Material.ENDER_CHEST;
            case MINE        -> Material.NETHERITE_PICKAXE;
            case CRAFT       -> Material.CRAFTING_TABLE;
            case FISH        -> Material.FISHING_ROD;
            case BREED       -> Material.WHEAT;
            case MARKET_SELL -> Material.GOLD_INGOT;
            case MARKET_BUY  -> Material.EMERALD;
            case MOD_SELL    -> Material.NETHER_STAR;
        };
    }
}
