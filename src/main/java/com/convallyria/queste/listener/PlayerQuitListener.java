package com.convallyria.queste.listener;

import com.convallyria.queste.Queste;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final Queste plugin;

    public PlayerQuitListener(Queste plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (plugin.getManagers().getStorageManager().getCachedAccounts().containsKey(player.getUniqueId())) {
            plugin.getManagers().getStorageManager().removeCachedAccount(player.getUniqueId());
        }
    }
}
