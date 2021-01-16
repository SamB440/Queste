package com.convallyria.queste.quest.reward;

import com.convallyria.queste.Queste;
import com.convallyria.queste.managers.registry.QuesteRegistry;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;

public final class QuestRewardRegistry extends QuesteRegistry<QuestReward> {

    @Nullable
    public QuestReward getNew(Class<? extends QuestReward> clazz, Queste plugin) {
        try {
            Constructor<?> constructor = clazz.getConstructor(Queste.class);
            return (QuestReward) constructor.newInstance(plugin);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getRegistryName() {
        return "rewards";
    }
}
