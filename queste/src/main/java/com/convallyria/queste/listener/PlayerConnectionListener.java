package com.convallyria.queste.listener;

import com.convallyria.queste.Queste;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

    private final Queste plugin;

    public PlayerConnectionListener(Queste plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getManagers().getStorageManager().getAccount(player.getUniqueId()); // Force load
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (plugin.getManagers().getStorageManager().getCachedAccounts().containsKey(player.getUniqueId())) {
            plugin.getManagers().getStorageManager().removeCachedAccount(player.getUniqueId());
        }
    }
}
