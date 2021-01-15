package com.convallyria.queste.managers.registry;

import com.convallyria.queste.Queste;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class QuestRegistry<T> {

    private final Map<String, Class<? extends T>> registeredClasses;

    protected QuestRegistry() {
        this.registeredClasses = new ConcurrentHashMap<>();
    }

    @NotNull
    protected Map<String, Class<? extends T>> getRegisteredClasses() {
        return registeredClasses;
    }

    @NotNull
    public ImmutableMap<String, Class<? extends T>> get() {
        return ImmutableMap.copyOf(registeredClasses);
    }

    public void register(Class<? extends T> clazz) {
        if (registeredClasses.containsKey(clazz.getSimpleName()))
            throw new IllegalStateException(clazz.getSimpleName() + " is already registered!");
        registeredClasses.put(clazz.getSimpleName(), clazz);
    }

    @Nullable
    public T getNew(String name, Queste plugin) {
        return getNew(registeredClasses.get(name), plugin);
    }

    @Nullable
    public abstract T getNew(Class<? extends T> clazz, Queste plugin);

}
