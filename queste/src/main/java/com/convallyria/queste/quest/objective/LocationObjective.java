package com.convallyria.queste.quest.objective;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.gui.GuiEditable;
import com.convallyria.queste.gui.element.ICustomGuiFeedback;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.utils.LocationUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class LocationObjective extends QuestObjective implements ICustomGuiFeedback {

    @GuiEditable(value = "Locations", type = GuiEditable.GuiEditableType.CHAT)
    private List<Location> locations;

    protected LocationObjective(IQuesteAPI api, Quest quest) {
        super(api, quest);
        this.locations = new ArrayList<>();
    }

    public void addLocation(Location location) {
        locations.add(location);
    }

    public List<Location> getLocations() {
        return locations;
    }

    @Override
    public boolean feedback(Player player, String input) {
        Location location = LocationUtils.getLocationFromInput(player, input);
        if (location == null) {
            player.sendMessage(ChatColor.RED + "Location could not be set: return value was null.");
            return true;
        }
        // Test if it already exists
        for (Location testLocation : locations) {
            int testX = testLocation.getBlockX();
            int testY = testLocation.getBlockY();
            int testZ = testLocation.getBlockZ();
            if (location.getBlockX() == testX && location.getBlockY() == testY && location.getBlockZ() == testZ) {
                locations.remove(testLocation);
                return true;
            }
        }

        addLocation(location);
        player.sendMessage(ChatColor.GREEN + "Location added.");
        return true;
    }

    @Override
    public String info(String field) {
        return "Enter 'TARGET' to set to eye location, 'SELF' for your location, or enter chat coordinates, " +
                "e.g 'x;y;z;yaw;pitch' (yaw/pitch optional). If the location already exists, it will be removed.";
    }
}
