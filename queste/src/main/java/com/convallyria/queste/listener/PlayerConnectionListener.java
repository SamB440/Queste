package com.convallyria.queste.listener;

import com.convallyria.queste.Queste;
import com.convallyria.queste.config.Configurations;
import com.convallyria.queste.items.QuestJournal;
import com.convallyria.queste.managers.data.account.QuesteAccount;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerConnectionListener implements Listener {

    private final Queste plugin;

    public PlayerConnectionListener(Queste plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        try {
            QuesteAccount account = plugin.getManagers().getStorageManager().getAccount(uuid).get();
            if (account != null) return;
        } catch (Exception e) {
            e.printStackTrace();
        }

        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Queste account could not be loaded.");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!Configurations.JOURNAL_ENABLED.getBoolean()) return;
        plugin.getManagers().getStorageManager().getAccount(player.getUniqueId()).thenAccept(account -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.getInventory().setItem(Configurations.JOURNAL_SLOT.getInt(), QuestJournal.getQuestJournal(account));
            });
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (plugin.getManagers().getStorageManager().getCachedAccounts().containsKey(player.getUniqueId())) {
            plugin.getManagers().getStorageManager().removeCachedAccount(player.getUniqueId());
        }
    }
}
