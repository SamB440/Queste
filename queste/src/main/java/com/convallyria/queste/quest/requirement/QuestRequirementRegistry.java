package com.convallyria.queste.quest.requirement;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.managers.registry.QuesteRegistry;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.List;

public final class QuestRequirementRegistry extends QuesteRegistry<QuestRequirement> {

    @Override
    public @Nullable QuestRequirement getNew(Class<? extends QuestRequirement> clazz, IQuesteAPI plugin, Object... data) {
        try {
            Constructor<?> constructor = clazz.getConstructor(IQuesteAPI.class);
            return (QuestRequirement) constructor.newInstance(plugin);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getRegistryName() {
        return "Requirements";
    }

    @Override
    public Class<QuestRequirement> getImplementation() {
        return QuestRequirement.class;
    }

    @Override
    public Material getIcon() {
        return Material.COMPARATOR;
    }

    @Override
    public List<String> getDescription() {
        return List.of("&7Requirements before a quest can", "&7be started by a player", "&e&lClick &7to edit quest requirements.");
    }
}
