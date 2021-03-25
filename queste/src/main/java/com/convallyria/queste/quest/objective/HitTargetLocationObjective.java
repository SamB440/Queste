package com.convallyria.queste.quest.objective;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.quest.Quest;
import org.bukkit.Location;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;

public final class HitTargetLocationObjective extends LocationObjective {

    public HitTargetLocationObjective(IQuesteAPI api, Quest quest) {
        super(api, quest);
    }

    public HitTargetLocationObjective(IQuesteAPI api, Quest quest, Location location) {
        super(api, quest);
        getLocations().add(location);
    }

    @EventHandler
    public void onMove(ProjectileHitEvent event) {
        Entity entity = event.getEntity();
        if (event.getHitBlock() == null) return;
        if (entity instanceof AbstractArrow) {
            AbstractArrow arrow = (AbstractArrow) entity;
            if (arrow.getShooter() instanceof Player) {
                Player player = (Player) arrow.getShooter();
                Location hitBlock = event.getHitBlock().getLocation();
                int x = hitBlock.getBlockX();
                int y = hitBlock.getBlockY();
                int z = hitBlock.getBlockZ();
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
    }

    @Override
    public String getName() {
        return "Hit Target";
    }
}
