package ru.voidrp.dailyquests.quest;

public final class ActiveQuest {

    public String templateId;
    public String displayName;
    public String description;
    public QuestType type;
    public String target;
    public int required;
    public int progress;
    public double moneyReward;
    public int expReward;
    public boolean rewardClaimed;

    public ActiveQuest() {}

    public static ActiveQuest from(QuestTemplate t) {
        ActiveQuest q = new ActiveQuest();
        q.templateId   = t.id;
        q.displayName  = t.displayName;
        q.description  = t.description;
        q.type         = t.type;
        q.target       = t.target;
        q.required     = t.required;
        q.progress     = 0;
        q.moneyReward  = t.moneyReward;
        q.expReward    = t.expReward;
        q.rewardClaimed = false;
        return q;
    }

    public boolean isCompleted() {
        return progress >= required;
    }

    public boolean isClaimable() {
        return isCompleted() && !rewardClaimed;
    }

    public int addProgress(int amount) {
        int prev = progress;
        progress = Math.min(required, progress + amount);
        return progress - prev;
    }

    public String progressBar() {
        int filled = required > 0 ? Math.min(10, progress * 10 / required) : 10;
        return "§a" + "█".repeat(filled) + "§8" + "░".repeat(10 - filled);
    }
}
