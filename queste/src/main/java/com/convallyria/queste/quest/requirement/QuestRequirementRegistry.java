package com.convallyria.queste.quest.requirement;

import com.convallyria.queste.Queste;
import com.convallyria.queste.managers.registry.QuesteRegistry;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;

public final class QuestRequirementRegistry extends QuesteRegistry<QuestRequirement> {

    @Override
    public @Nullable QuestRequirement getNew(Class<? extends QuestRequirement> clazz, Queste plugin) {
        try {
            Constructor<?> constructor = clazz.getConstructor(Queste.class);
            return (QuestRequirement) constructor.newInstance(plugin);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getRegistryName() {
        return "requirements";
    }

    @Override
    public Class<QuestRequirement> getImplementation() {
        return QuestRequirement.class;
    }
}
