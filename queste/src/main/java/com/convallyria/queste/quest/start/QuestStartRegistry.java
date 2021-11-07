package com.convallyria.queste.quest.start;

import com.convallyria.queste.Queste;
import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.managers.registry.QuesteRegistry;
import com.convallyria.queste.quest.Quest;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.List;

public final class QuestStartRegistry extends QuesteRegistry<QuestStart> {

    @Nullable
    public QuestStart getNewStarter(String name, Queste plugin, Quest quest) {
        return getNewStarter(getRegisteredClasses().get(name), plugin, quest);
    }

    @Nullable
    public QuestStart getNewStarter(Class<? extends QuestStart> clazz, IQuesteAPI plugin, Quest quest) {
        try {
            Constructor<?> constructor = clazz.getConstructor(IQuesteAPI.class, Quest.class);
            return (QuestStart) constructor.newInstance(plugin, quest);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @deprecated Not the recommended way to generate a new starter.
     * {@link #getNewStarter(String, Queste, Quest)} {@link #getNewStarter(Class, IQuesteAPI, Quest)}
     */
    @Deprecated
    @Override
    public @Nullable QuestStart getNew(Class<? extends QuestStart> clazz, IQuesteAPI plugin, Object... data) {
        return getNewStarter(clazz, plugin, (Quest) data[0]);
    }

    @Override
    public String getRegistryName() {
        return "Starters";
    }

    @Override
    public Class<QuestStart> getImplementation() {
        return QuestStart.class;
    }

    @Override
    public Material getIcon() {
        return Material.STICK;
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7Starters for how the quest", "&7can be started by a player", "&e&lClick &7to edit quest starters.");
    }
}
