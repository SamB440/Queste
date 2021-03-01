package com.convallyria.queste.quest.objective;

import com.convallyria.queste.Queste;
import com.convallyria.queste.gui.GuiEditable;
import com.convallyria.queste.quest.Quest;

public abstract class RegionObjective extends QuestObjective {

    @GuiEditable("Region")
    private String region;

    protected RegionObjective(Queste plugin, Quest quest) {
        super(plugin, quest);
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegion() {
        return region;
    }
}
