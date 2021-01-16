package com.convallyria.queste.quest.start;

import com.convallyria.queste.Queste;
import com.convallyria.queste.managers.data.account.QuesteAccount;
import com.convallyria.queste.quest.Quest;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;

public final class QuestQuestRequirement extends QuestRequirement {

    private String questName;

    protected QuestQuestRequirement(Queste plugin) {
        super(plugin);
        this.questName = "example";
    }

    public String getQuestName() {
        return questName;
    }

    public void setQuestName(String questName) {
        this.questName = questName;
    }

    @Override
    public boolean meetsRequirements(Player player) {
        try {
            QuesteAccount account = getPlugin().getManagers().getStorageManager().getAccount(player.getUniqueId()).get();
            for (Quest completedQuest : account.getCompletedQuests()) {
                if (completedQuest.getName().equals(questName)) return true;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getName() {
        return "Quest";
    }
}
