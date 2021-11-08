package com.convallyria.queste.quest.objective;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.gui.GuiEditable;
import com.convallyria.queste.quest.Quest;
import org.bukkit.Material;

public abstract class RegionObjective extends QuestObjective {

    @GuiEditable(value = "Region", icon = Material.PAPER)
    private String region;

    protected RegionObjective(IQuesteAPI api, Quest quest) {
        super(api, quest);
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegion() {
        return region;
    }
}
