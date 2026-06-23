package ru.voidrp.dailyquests.quest;

import java.util.ArrayList;
import java.util.List;

public final class DeliveryActiveQuest {

    public String           templateId;
    public String           displayName;
    public String           description;
    public List<RequiredItem> required = new ArrayList<>();
    public double           moneyReward;
    public int              expReward;
    public boolean          rewardClaimed;

    public DeliveryActiveQuest() {}

    public static DeliveryActiveQuest from(DeliveryQuestTemplate t) {
        DeliveryActiveQuest q = new DeliveryActiveQuest();
        q.templateId   = t.id;
        q.displayName  = t.displayName;
        q.description  = t.description;
        q.required     = new ArrayList<>(t.required);
        q.moneyReward  = t.moneyReward;
        q.expReward    = t.expReward;
        q.rewardClaimed = false;
        return q;
    }

    public boolean isCompleted() { return rewardClaimed; }
}
