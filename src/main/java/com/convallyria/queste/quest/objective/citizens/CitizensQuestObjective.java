package com.convallyria.queste.quest.objective.citizens;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.quest.objective.QuestObjective;

public abstract class CitizensQuestObjective extends QuestObjective {

    private int npcId;

    protected CitizensQuestObjective(Queste plugin, Quest quest) {
        super(plugin, quest);
    }

    public int getNpcId() {
        return npcId;
    }

    public void setNpcId(int npcId) {
        this.npcId = npcId;
    }
}
