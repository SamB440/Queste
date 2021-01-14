package com.convallyria.queste.quest.objective;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class QuestObjectiveRegistry {

    private final Queste plugin;
    private final Map<String, Class<? extends QuestObjective>> objectives;

    public QuestObjectiveRegistry(Queste plugin) {
        this.plugin = plugin;
        this.objectives = new ConcurrentHashMap<>();
    }

    public ImmutableMap<String, Class<? extends QuestObjective>> getObjectives() {
        return ImmutableMap.copyOf(objectives);
    }

    public void registerObjective(Class<? extends QuestObjective> clazz) {
        if (objectives.containsKey(clazz.getSimpleName()))
            throw new IllegalStateException("Objective " + clazz.getSimpleName() + " is already registered!");
        objectives.put(clazz.getSimpleName(), clazz);
    }

    @Nullable
    public QuestObjective getNewObjective(String name, Queste plugin, Quest quest) {
        return getNewObjective(objectives.get(name), plugin, quest);
    }

    @Nullable
    public QuestObjective getNewObjective(Class<? extends QuestObjective> clazz, Queste plugin, Quest quest) {
        try {
            Constructor<?> constructor = clazz.getConstructor(Queste.class, Quest.class);
            QuestObjective objective = (QuestObjective) constructor.newInstance(plugin, quest);
            if (objective.getPluginRequirement() != null
            && Bukkit.getPluginManager().getPlugin(objective.getPluginRequirement()) == null) {
                return null;
            }
            return objective;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
