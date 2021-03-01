package com.convallyria.queste.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class LocationUtils {

    @Nullable
    public static Location getLocationFromInput(Player player, String input) {
        String upperInput = input.toUpperCase();
        switch (upperInput) {
            case "SELF":
                return player.getLocation();
            case "TARGET":
                Block block = player.getTargetBlockExact(6);
                if (block != null) return block.getLocation();
                return null;
            default:
                String[] split = upperInput.split(";");
                int x = Integer.parseInt(split[0]);
                int y = Integer.parseInt(split[1]);
                int z = Integer.parseInt(split[2]);
                float yaw = player.getLocation().getYaw();
                float pitch = player.getLocation().getPitch();
                if (split.length > 3) {
                    yaw = Float.parseFloat(split[3]);
                }
                if (split.length > 4) {
                    pitch = Float.parseFloat(split[4]);
                }
                World world = player.getWorld();
                return new Location(world, x, y, z, yaw, pitch);
        }
    }
}
