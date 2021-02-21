package com.convallyria.queste.quest.reward;

import com.convallyria.queste.Queste;
import com.convallyria.queste.managers.registry.QuesteRegistry;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;

public final class QuestRewardRegistry extends QuesteRegistry<QuestReward> {

    @Nullable
    public QuestReward getNew(Class<? extends QuestReward> clazz, Queste plugin, Object... data) {
        try {
            Constructor<?> constructor = clazz.getConstructor();
            return (QuestReward) constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getRegistryName() {
        return "rewards";
    }

    @Override
    public Class<QuestReward> getImplementation() {
        return QuestReward.class;
    }
}
