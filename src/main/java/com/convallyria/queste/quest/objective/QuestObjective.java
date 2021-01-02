package com.convallyria.queste.quest.objective;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class QuestObjective implements Listener {

    private transient Queste plugin;

    private final String questName;
    private final QuestObjectiveEnum type;
    private int completionAmount;
    private final Map<UUID, Integer> progress;

    public QuestObjective(Queste plugin, @NotNull QuestObjectiveEnum type, Quest quest) {
        this.plugin = plugin;
        this.questName = quest.getName();
        this.type = type;
        this.completionAmount = 10;
        this.progress = new ConcurrentHashMap<>();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @NotNull
    public Queste getPlugin() {
        if (plugin == null) this.plugin = JavaPlugin.getPlugin(Queste.class);
        return plugin;
    }

    @NotNull
    public String getQuestName() {
        return questName;
    }

    @Nullable
    public Quest getQuest() {
        return getPlugin().getManagers().getQuesteCache().getQuest(getQuestName());
    }

    @NotNull
    public QuestObjectiveEnum getType() {
        return type;
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
        player.sendMessage("status: " + this.getIncrement(player) + " / " + this.getCompletionAmount());
        if (progress.get(player.getUniqueId()) >= completionAmount) {
            Quest quest = getQuest();
            if (quest != null) {
                quest.tryComplete(player);
            } else {
                plugin.getLogger().warning("Unable to find quest " + getQuestName());
            }
        }
    }

    public void setIncrement(@NotNull Player player, int increment) {
        progress.put(player.getUniqueId(), increment);
    }

    public boolean hasCompleted(@NotNull Player player) {
        return progress.getOrDefault(player.getUniqueId(), 0) == completionAmount;
    }

    public enum QuestObjectiveEnum {
        PLACE_BLOCK(PlaceBlockQuestObjective.class, "Place Block"),
        BREAK_BLOCK(BreakBlockQuestObjective.class, "Break Block");

        private final Class<? extends QuestObjective> clazz;
        private final String name;

        QuestObjectiveEnum(Class<? extends QuestObjective> clazz, String name) {
            this.clazz = clazz;
            this.name = name;
        }

        public QuestObjective getNewObjective(Queste plugin, Quest quest) {
            try {
                Constructor<?> constructor = clazz.getConstructor(Queste.class, Quest.class);
                return (QuestObjective) constructor.newInstance(plugin, quest);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
            return null;
        }

        public String getName() {
            return name;
        }
    }
}
