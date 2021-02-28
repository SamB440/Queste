package com.convallyria.queste.quest.objective;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public abstract class LocationObjective extends QuestObjective {

    private List<Location> locations;

    protected LocationObjective(Queste plugin, Quest quest) {
        super(plugin, quest);
        this.locations = new ArrayList<>();
    }

    public void addLocation(Location location) {
        locations.add(location);
    }

    public List<Location> getLocations() {
        return locations;
    }
}
