package com.convallyria.queste.quest.objective;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.gui.element.ICustomGuiFeedback;
import com.convallyria.queste.quest.Quest;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public final class ReachLocationObjective extends LocationObjective implements ICustomGuiFeedback {

    public ReachLocationObjective(IQuesteAPI api, Quest quest) {
        super(api, quest);
    }

    public ReachLocationObjective(IQuesteAPI api, Quest quest, Location location) {
        super(api, quest);
        addLocation(location);
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getTo() == null) return;
        if (event.getTo().distanceSquared(event.getFrom()) > 0) {
            if (hasCompleted(player)) return;
            Location location = event.getTo();
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();
            getLocations().forEach(requiredLocation -> {
                int requiredX = requiredLocation.getBlockX();
                int requiredY = requiredLocation.getBlockY();
                int requiredZ = requiredLocation.getBlockZ();
                if (x == requiredX && y == requiredY && z == requiredZ) {
                    this.increment(player);
                }
            });
        }
    }

    @Override
    public String getName() {
        return "Reach Location";
    }
}
