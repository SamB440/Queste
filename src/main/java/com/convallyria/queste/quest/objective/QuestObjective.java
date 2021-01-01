package com.convallyria.queste.quest.objective;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class QuestObjective implements Listener {

    private final transient Queste plugin;

    private final String questName;
    private final String name;
    private int completionAmount;
    private Map<UUID, Integer> progress;

    public QuestObjective(Queste plugin, @NotNull String name, Quest quest) {
        this.plugin = plugin;
        this.questName = quest.getName();
        this.name = name;
        this.completionAmount = 10;
        this.progress = new ConcurrentHashMap<>();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public Queste getPlugin() {
        return plugin;
    }

    public String getQuestName() {
        return questName;
    }

    public String getName() {
        return name;
    }

    public int getCompletionAmount() {
        return completionAmount;
    }

    public void setCompletionAmount(int completionAmount) {
        this.completionAmount = completionAmount;
    }

    public int getIncrement(@NotNull Player player) {
        return progress.getOrDefault(player.getUniqueId(), 0);
    }

    public void increment(@NotNull Player player) {
        progress.put(player.getUniqueId(), progress.getOrDefault(player.getUniqueId(), 0) + 1);
    }
}
