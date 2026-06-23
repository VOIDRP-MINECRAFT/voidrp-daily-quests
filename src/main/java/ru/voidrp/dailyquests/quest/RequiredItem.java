package ru.voidrp.dailyquests.quest;

public final class RequiredItem {
    public String material;    // Bukkit Material name, e.g. "NETHER_STAR"
    public int    amount;
    public String displayName; // Russian label for GUI

    public RequiredItem() {}
    public RequiredItem(String material, int amount, String displayName) {
        this.material    = material;
        this.amount      = amount;
        this.displayName = displayName;
    }
}
