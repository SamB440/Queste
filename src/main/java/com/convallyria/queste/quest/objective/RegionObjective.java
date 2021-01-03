package com.convallyria.queste.quest.objective;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import org.jetbrains.annotations.NotNull;

public abstract class RegionObjective extends QuestObjective {

    private String region;

    public RegionObjective(Queste plugin, @NotNull QuestObjectiveEnum type, Quest quest) {
        super(plugin, type, quest);
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegion() {
        return region;
    }
}
