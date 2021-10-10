package com.convallyria.queste.quest.objective.citizens;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.gui.GuiEditable;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.quest.objective.QuestObjective;

public abstract class CitizensQuestObjective extends QuestObjective {

    @GuiEditable("NPC id")
    private int npcId;

    protected CitizensQuestObjective(IQuesteAPI api, Quest quest) {
        super(api, quest);
    }

    public int getNpcId() {
        return npcId;
    }

    public void setNpcId(int npcId) {
        this.npcId = npcId;
    }

    @Override
    public String getPluginRequirement() {
        return "Citizens";
    }
}
