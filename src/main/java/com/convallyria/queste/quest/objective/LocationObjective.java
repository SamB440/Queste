package com.convallyria.queste.quest.objective;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public abstract class LocationObjective extends QuestObjective {

    private Location location;

    public LocationObjective(Queste plugin, @NotNull QuestObjectiveEnum type, Quest quest) {
        super(plugin, type, quest);
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
