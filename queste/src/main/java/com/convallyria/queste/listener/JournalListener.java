package com.convallyria.queste.listener;

import com.convallyria.queste.Queste;
import com.convallyria.queste.config.Configurations;
import com.convallyria.queste.items.QuestJournal;
import com.convallyria.queste.managers.data.account.QuesteAccount;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;

public class JournalListener implements Listener {

    private final Queste plugin;

    public JournalListener(Queste plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) throws ExecutionException, InterruptedException {
        final Player player = event.getPlayer();
        final ItemStack item = event.getOffHandItem();
        if (item == null) return;
        QuesteAccount account = plugin.getManagers().getStorageManager().getAccount(player.getUniqueId()).get();
        if (QuestJournal.isJournal(account, item)) event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) throws ExecutionException, InterruptedException {
        final Player player = event.getPlayer();
        final ItemStack item = event.getItemDrop().getItemStack();
        QuesteAccount account = plugin.getManagers().getStorageManager().getAccount(player.getUniqueId()).get();
        if (QuestJournal.isJournal(account, item)) event.setCancelled(true);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) throws ExecutionException, InterruptedException {
        final Player player = (Player) event.getView().getPlayer();
        final ItemStack item = event.getCurrentItem();
        if (item == null) return;
        QuesteAccount account = plugin.getManagers().getStorageManager().getAccount(player.getUniqueId()).get();
        if (QuestJournal.isJournal(account, item)) event.setCancelled(true);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) throws ExecutionException, InterruptedException {
        final Player player = event.getEntity();
        Iterator<ItemStack> iterator = event.getDrops().iterator();
        while (iterator.hasNext()) {
            ItemStack item = iterator.next();
            QuesteAccount account = plugin.getManagers().getStorageManager().getAccount(player.getUniqueId()).get();
            if (QuestJournal.isJournal(account, item)) iterator.remove();
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) throws ExecutionException, InterruptedException {
        Player player = event.getPlayer();
        QuesteAccount account = plugin.getManagers().getStorageManager().getAccount(player.getUniqueId()).get();
        player.getInventory().setItem(Configurations.JOURNAL_SLOT.getInt(), QuestJournal.getQuestJournal(account));
    }
}
