package com.convallyria.queste.managers.registry;

import com.convallyria.queste.api.IQuesteAPI;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IQuesteRegistry<T> {

    @NotNull
    ImmutableMap<String, Class<? extends T>> get();

    @NotNull
    List<String> loadAllPresets();

    void savePreset(Object toSave, String name);

    @Nullable
    T loadPreset(String name);

    /**
     * Attempts to register a class.
     * @param clazz class to register
     * @throws IllegalArgumentException if class is already registered
     */
    void register(Class<? extends T> clazz);

    @Nullable
    T getNew(String name, IQuesteAPI plugin, Object... data);

    @Nullable
    T getNew(Class<? extends T> clazz, IQuesteAPI plugin, Object... data);

    String getRegistryName();

    Class<T> getImplementation();

    Material getIcon();

    List<String> getDescription();

}
