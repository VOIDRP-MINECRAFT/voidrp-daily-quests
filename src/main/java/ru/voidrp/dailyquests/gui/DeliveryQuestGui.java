package ru.voidrp.dailyquests.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.voidrp.dailyquests.player.DeliveryQuestStorage;
import ru.voidrp.dailyquests.quest.DeliveryActiveQuest;
import ru.voidrp.dailyquests.quest.RequiredItem;

import java.util.ArrayList;
import java.util.List;

public final class DeliveryQuestGui {

    public static final String TITLE       = "§5§l📦 Торговец Артефактами";
    public static final int    SUBMIT_SLOT = 40;

    private DeliveryQuestGui() {}

    public static Inventory build(Player player, DeliveryActiveQuest quest) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE);

        ItemStack border = glass(Material.PURPLE_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 54; i++) inv.setItem(i, border);

        if (quest == null) {
            inv.setItem(22, noQuest());
            return inv;
        }

        if (quest.isCompleted()) {
            inv.setItem(22, completedItem(quest));
            return inv;
        }

        // Up to 7 required items, centered in rows 1-2
        int[] itemSlots = {10, 12, 14, 16, 19, 22, 25};
        List<RequiredItem> reqs = quest.required;
        for (int i = 0; i < Math.min(reqs.size(), itemSlots.length); i++) {
            inv.setItem(itemSlots[i], requirementItem(player, reqs.get(i)));
        }

        inv.setItem(4,  timerItem());
        inv.setItem(49, infoItem(quest));
        inv.setItem(SUBMIT_SLOT, submitButton(canSubmit(player, quest), quest));

        return inv;
    }

    private static ItemStack requirementItem(Player player, RequiredItem req) {
        int inInventory = countInInventory(player, req.material);
        boolean has = inInventory >= req.amount;

        ItemStack item = new ItemStack(iconFor(req.material));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName((has ? "§a✔ " : "§c✘ ") + req.displayName);

        List<String> lore = new ArrayList<>();
        lore.add("§7Нужно:  §f" + req.amount);
        lore.add("§7У тебя: " + (has ? "§a" : "§c") + inInventory);
        if (!has) lore.add("§7Не хватает: §c" + (req.amount - inInventory));
        if (req.material.contains(":") && !req.material.startsWith("minecraft:"))
            lore.add("§8[мод: " + req.material.split(":")[0] + "]");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack submitButton(boolean canSubmit, DeliveryActiveQuest quest) {
        ItemStack item = new ItemStack(canSubmit ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (canSubmit) {
            meta.setDisplayName("§a§l✦ СДАТЬ ПРЕДМЕТЫ И ПОЛУЧИТЬ НАГРАДУ ✦");
            List<String> lore = new ArrayList<>();
            lore.add("§6+" + (long) quest.moneyReward + " монет");
            lore.add("§b+" + quest.expReward + " опыта");
            lore.add("");
            lore.add("§7Предметы будут изъяты из инвентаря!");
            meta.setLore(lore);
        } else {
            meta.setDisplayName("§c✘ Соберите все предметы");
            List<String> lore = new ArrayList<>();
            lore.add("§7Не хватает части предметов.");
            lore.add("§7Проверьте список выше.");
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack timerItem() {
        ItemStack item = new ItemStack(Material.CLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§d§lДо обновления задания:");
        List<String> lore = new ArrayList<>();
        int days = DeliveryQuestStorage.daysUntilReset();
        lore.add("§f" + days + " §7" + (days == 1 ? "день" : days < 5 ? "дня" : "дней"));
        lore.add("§8Новое задание каждые 7 дней");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack infoItem(DeliveryActiveQuest quest) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§5§l" + quest.displayName);
        List<String> lore = new ArrayList<>();
        lore.add("§7" + quest.description);
        lore.add("");
        lore.add("§6Награда: §f" + (long) quest.moneyReward + " монет + §b" + quest.expReward + " опыта");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack completedItem(DeliveryActiveQuest quest) {
        ItemStack item = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§a§l✔ Задание выполнено!");
        List<String> lore = new ArrayList<>();
        lore.add("§7" + quest.displayName);
        lore.add("");
        lore.add("§7Возвращайся через §f" + DeliveryQuestStorage.daysUntilReset() + " §7дн. за новым заданием.");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack noQuest() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§cЗадание недоступно");
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

    // ── Public helpers ────────────────────────────────────────────────────────

    public static boolean canSubmit(Player player, DeliveryActiveQuest quest) {
        if (quest == null || quest.isCompleted()) return false;
        for (RequiredItem req : quest.required) {
            if (countInInventory(player, req.material) < req.amount) return false;
        }
        return true;
    }

    public static void takeItems(Player player, DeliveryActiveQuest quest) {
        ItemStack[] contents = player.getInventory().getContents();
        for (RequiredItem req : quest.required) {
            int toRemove = req.amount;
            for (int i = 0; i < contents.length && toRemove > 0; i++) {
                ItemStack stack = contents[i];
                if (stack == null || stack.getType() == Material.AIR) continue;
                if (!matchesMaterial(stack, req.material)) continue;
                int take = Math.min(stack.getAmount(), toRemove);
                int remaining = stack.getAmount() - take;
                if (remaining <= 0) {
                    player.getInventory().setItem(i, null);
                    contents[i] = null;
                } else {
                    stack.setAmount(remaining);
                }
                toRemove -= take;
            }
        }
        player.updateInventory();
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    /**
     * Counts items in inventory matching the given material ID.
     * Supports both vanilla Material names ("NETHER_STAR") and
     * namespaced mod keys ("iceandfire:fire_dragon_scale").
     */
    public static int countInInventory(Player player, String materialId) {
        int count = 0;
        for (ItemStack s : player.getInventory().getContents()) {
            if (s == null || s.getType() == Material.AIR) continue;
            if (matchesMaterial(s, materialId)) count += s.getAmount();
        }
        return count;
    }

    private static boolean matchesMaterial(ItemStack stack, String materialId) {
        if (materialId.contains(":")) {
            // Namespaced key: works for both vanilla ("minecraft:nether_star") and mods
            return stack.getType().getKey().toString().equalsIgnoreCase(materialId);
        }
        // Plain vanilla Material name
        try {
            return stack.getType() == Material.valueOf(materialId.toUpperCase());
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Picks a representative vanilla icon for the GUI slot.
     * For modded items we can't create a real ItemStack, so we derive from name keywords.
     */
    private static Material iconFor(String materialId) {
        // Try vanilla first
        if (!materialId.contains(":")) {
            try { return Material.valueOf(materialId.toUpperCase()); } catch (Exception ignored) {}
        }
        // minecraft: namespace
        if (materialId.startsWith("minecraft:")) {
            String name = materialId.substring("minecraft:".length()).toUpperCase();
            try { return Material.valueOf(name); } catch (Exception ignored) {}
        }
        // Derive icon from keywords in the mod item name
        String lower = materialId.toLowerCase();
        if (lower.contains("chaos"))         return Material.NETHER_STAR;
        if (lower.contains("heart"))         return Material.NETHER_STAR;
        if (lower.contains("awakened"))      return Material.AMETHYST_SHARD;
        if (lower.contains("draconium"))     return Material.PURPLE_DYE;
        if (lower.contains("dragon"))        return Material.FIRE_CHARGE;
        if (lower.contains("fire"))          return Material.BLAZE_POWDER;
        if (lower.contains("ice") || lower.contains("frost")) return Material.BLUE_ICE;
        if (lower.contains("scale"))         return Material.IRON_INGOT;
        if (lower.contains("ingot") || lower.contains("steel") || lower.contains("metal")) return Material.GOLD_INGOT;
        if (lower.contains("blood"))         return Material.REDSTONE;
        if (lower.contains("bone"))          return Material.BONE;
        if (lower.contains("tusk") || lower.contains("fang")) return Material.BONE;
        if (lower.contains("resin") || lower.contains("slime")) return Material.SLIME_BALL;
        if (lower.contains("feather"))       return Material.FEATHER;
        if (lower.contains("gem") || lower.contains("shard")) return Material.AMETHYST_SHARD;
        if (lower.contains("leaf"))          return Material.OAK_LEAVES;
        if (lower.contains("carminite"))     return Material.MAGENTA_DYE;
        if (lower.contains("naga"))          return Material.TURTLE_SCUTE;
        if (lower.contains("sea") || lower.contains("serpent")) return Material.PRISMARINE_SHARD;
        if (lower.contains("troll"))         return Material.MOSSY_COBBLESTONE;
        if (lower.contains("trophy"))        return Material.GOLDEN_HELMET;
        if (lower.contains("myrmex"))        return Material.HONEYCOMB;
        return Material.BOOK;
    }
}
