package ru.voidrp.dailyquests.player;

import ru.voidrp.dailyquests.quest.ActiveQuest;

import java.util.ArrayList;
import java.util.List;

public final class PlayerQuestState {

    public String lastResetDate = "";   // yyyy-MM-dd
    public List<ActiveQuest> quests = new ArrayList<>();

    public boolean allClaimed() {
        return quests.stream().allMatch(q -> !q.isCompleted() || q.rewardClaimed);
    }
}
