package ru.voidrp.dailyquests.quest;

public final class QuestTemplate {

    public final String id;
    public final String displayName;
    public final String description;
    public final QuestType type;
    /** Entity type key (e.g. "minecraft:zombie") for KILL/BREED, Material name for COLLECT/MINE/FISH */
    public final String target;
    public final int required;
    public final double moneyReward;
    public final int expReward;

    public QuestTemplate(String id, String displayName, String description,
                         QuestType type, String target,
                         int required, double moneyReward, int expReward) {
        this.id          = id;
        this.displayName = displayName;
        this.description = description;
        this.type        = type;
        this.target      = target;
        this.required    = required;
        this.moneyReward = moneyReward;
        this.expReward   = expReward;
    }
}
