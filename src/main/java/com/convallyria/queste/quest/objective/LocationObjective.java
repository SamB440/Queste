package com.convallyria.queste.quest.objective;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public abstract class LocationObjective extends QuestObjective {

    private Location location;

    protected LocationObjective(Queste plugin, Quest quest) {
        super(plugin, quest);
        this.location = new Location(Bukkit.getWorlds().get(0), 0, 100, 0);
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
