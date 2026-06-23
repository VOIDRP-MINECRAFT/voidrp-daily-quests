package ru.voidrp.dailyquests.quest;

import java.util.List;

public final class DeliveryQuestTemplate {
    public final String           id;
    public final String           displayName;
    public final String           description;
    public final List<RequiredItem> required;
    public final double           moneyReward;
    public final int              expReward;

    public DeliveryQuestTemplate(String id, String displayName, String description,
                                  List<RequiredItem> required,
                                  double moneyReward, int expReward) {
        this.id          = id;
        this.displayName = displayName;
        this.description = description;
        this.required    = required;
        this.moneyReward = moneyReward;
        this.expReward   = expReward;
    }
}
