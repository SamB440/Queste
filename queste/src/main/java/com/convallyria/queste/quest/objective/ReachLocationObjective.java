package com.convallyria.queste.quest.objective;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public final class ReachLocationObjective extends LocationObjective {

    public ReachLocationObjective(Queste plugin, Quest quest) {
        super(plugin, quest);
    }

    public ReachLocationObjective(Queste plugin, Quest quest, Location location) {
        super(plugin, quest);
        getLocations().add(location);
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
