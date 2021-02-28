package com.convallyria.queste.quest.objective;

import com.convallyria.queste.Queste;
import com.convallyria.queste.managers.registry.QuesteRegistry;
import com.convallyria.queste.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;

public final class QuestObjectiveRegistry extends QuesteRegistry<QuestObjective> {

    @Nullable
    public QuestObjective getNewObjective(String name, Queste plugin, Quest quest) {
        return getNewObjective(getRegisteredClasses().get(name), plugin, quest);
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

    /**
     * @deprecated Not the recommended way to generate a new objective.
     * {@link #getNewObjective(String, Queste, Quest)} {@link #getNewObjective(Class, Queste, Quest)}
     */
    @Deprecated
    @Override
    public @Nullable QuestObjective getNew(Class<? extends QuestObjective> clazz, Queste plugin, Object... data) {
        return getNewObjective(clazz, plugin, (Quest) data[0]);
    }

    @Override
    public String getRegistryName() {
        return "objectives";
    }

    @Override
    public Class<QuestObjective> getImplementation() {
        return QuestObjective.class;
    }

    @Override
    public Material getIcon() {
        return Material.WRITTEN_BOOK;
    }
}
