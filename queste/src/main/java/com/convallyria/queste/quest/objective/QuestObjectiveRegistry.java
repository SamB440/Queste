package com.convallyria.queste.quest.objective;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.managers.registry.QuesteRegistry;
import com.convallyria.queste.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.List;

public final class QuestObjectiveRegistry extends QuesteRegistry<QuestObjective> {

    @Nullable
    public QuestObjective getNewObjective(String name, IQuesteAPI plugin, Quest quest) {
        return getNewObjective(getRegisteredClasses().get(name), plugin, quest);
    }

    @Nullable
    public QuestObjective getNewObjective(Class<? extends QuestObjective> clazz, IQuesteAPI plugin, Quest quest) {
        try {
            Constructor<?> constructor = clazz.getConstructor(IQuesteAPI.class, Quest.class);
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

    /**
     * @deprecated Not the recommended way to generate a new objective.
     * {@link #getNewObjective(String, IQuesteAPI, Quest)} {@link #getNewObjective(Class, IQuesteAPI, Quest)}
     */
    @Deprecated
    @Override
    public @Nullable QuestObjective getNew(Class<? extends QuestObjective> clazz, IQuesteAPI plugin, Object... data) {
        return getNewObjective(clazz, plugin, (Quest) data[0]);
    }

    @Override
    public String getRegistryName() {
        return "Objectives";
    }

    @Override
    public Class<QuestObjective> getImplementation() {
        return QuestObjective.class;
    }

    @Override
    public Material getIcon() {
        return Material.WRITABLE_BOOK;
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7Tasks required to complete the quest", "&e&lClick &7to edit quest objectives.");
    }
}
