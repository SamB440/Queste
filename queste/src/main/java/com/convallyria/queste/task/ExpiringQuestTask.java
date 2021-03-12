package com.convallyria.queste.task;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class ExpiringQuestTask implements Runnable {

    private final Queste plugin;

    public ExpiringQuestTask(Queste plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            plugin.getManagers().getStorageManager().getAccount(player.getUniqueId()).thenAccept(account -> {
                for (Quest quest : account.getActiveQuests()) {
                    if (quest.getTime() == 0) continue;
                    long time = TimeUtils.convertTicks(quest.getTime(), TimeUnit.MILLISECONDS);
                    if (System.currentTimeMillis() >= (account.getStartTime(quest) + time)) {
                        account.removeActiveQuest(quest);
                        player.sendMessage("quest failed");
                    }
                }
            });
        }
    }
}
