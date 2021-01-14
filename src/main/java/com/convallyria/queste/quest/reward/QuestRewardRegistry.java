package com.convallyria.queste.quest.reward;

import com.convallyria.queste.Queste;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class QuestRewardRegistry {

    private final Queste plugin;
    private final Map<String, Class<? extends QuestReward>> rewards;

    public QuestRewardRegistry(Queste plugin) {
        this.plugin = plugin;
        this.rewards = new ConcurrentHashMap<>();
    }

    public ImmutableMap<String, Class<? extends QuestReward>> getRewards() {
        return ImmutableMap.copyOf(rewards);
    }

    public void registerReward(Class<? extends QuestReward> clazz) {
        if (rewards.containsKey(clazz.getSimpleName()))
            throw new IllegalStateException("Objective " + clazz.getSimpleName() + " is already registered!");
        rewards.put(clazz.getSimpleName(), clazz);
    }

    @Nullable
    public QuestReward getNewReward(String name, Queste plugin) {
        return getNewReward(rewards.get(name), plugin);
    }

    @Nullable
    public QuestReward getNewReward(Class<? extends QuestReward> clazz, Queste plugin) {
        try {
            Constructor<?> constructor = clazz.getConstructor(Queste.class);
            return (QuestReward) constructor.newInstance(plugin);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
