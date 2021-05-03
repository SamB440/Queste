package com.convallyria.queste.task;

import com.convallyria.queste.Queste;
import com.convallyria.queste.config.Configurations;
import com.convallyria.queste.items.QuestJournal;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class JournalUpdateTask implements Runnable {

    private final Queste plugin;

    public JournalUpdateTask(final Queste plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            plugin.getManagers().getStorageManager().getAccount(player.getUniqueId()).thenAccept(account -> {
                if (!Configurations.JOURNAL_ENABLED.getBoolean()) return;
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.getInventory().setItem(Configurations.JOURNAL_SLOT.getInt(), QuestJournal.getQuestJournal(account));
                });
            });
        }
    }
}
