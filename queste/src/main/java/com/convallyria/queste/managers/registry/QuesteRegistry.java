package com.convallyria.queste.managers.registry;

import com.convallyria.queste.Queste;
import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.gson.AbstractAdapter;
import com.convallyria.queste.gson.ConfigurationSerializableAdapter;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class QuesteRegistry<T> implements IQuesteRegistry<T> {

    private final Map<String, Class<? extends T>> registeredClasses;

    protected QuesteRegistry() {
        this.registeredClasses = new ConcurrentHashMap<>();
    }

    @NotNull
    protected Map<String, Class<? extends T>> getRegisteredClasses() {
        return registeredClasses;
    }

    @NotNull
    @Override
    public ImmutableMap<String, Class<? extends T>> get() {
        return ImmutableMap.copyOf(registeredClasses);
    }

    @NotNull
    @Override
    public List<String> loadAllPresets() {
        List<String> names = new ArrayList<>();
        Queste plugin = JavaPlugin.getPlugin(Queste.class);
        File preset = new File(plugin.getManagers().getPresetFolder(this) + File.separator);
        if (!preset.exists()) return names;
        for (File file : preset.listFiles()) {
            if (!file.getName().endsWith(".json")) continue;
            names.add(file.getName().replace(".json", ""));
        }
        return names;
    }

    @Override
    public void savePreset(Object toSave, String name) {
        Queste plugin = JavaPlugin.getPlugin(Queste.class);
        File preset = new File(plugin.getManagers().getPresetFolder(this) + File.separator + name + ".json");
        try {
            try (Writer writer = new FileWriter(preset)) {
                Gson gson = new GsonBuilder()
                        .registerTypeHierarchyAdapter(getImplementation(), new AbstractAdapter<T>(null))
                        .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ConfigurationSerializableAdapter())
                        .setPrettyPrinting()
                        .serializeNulls()
                        .create();
                gson.toJson(toSave, writer);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public T loadPreset(String name) {
        Queste plugin = JavaPlugin.getPlugin(Queste.class);
        File preset = new File(plugin.getManagers().getPresetFolder(this) + File.separator + name + ".json");
        if (!preset.exists()) return null;
        try {
            T implementedClass;
            try (FileReader reader = new FileReader(preset)) {
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(getImplementation(), new AbstractAdapter<T>(null))
                        .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ConfigurationSerializableAdapter())
                        .setPrettyPrinting()
                        .serializeNulls()
                        .create();
                implementedClass = gson.fromJson(reader, getImplementation());
            }
            return implementedClass;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void register(Class<? extends T> clazz) {
        if (registeredClasses.containsKey(clazz.getSimpleName()))
            throw new IllegalArgumentException(clazz.getSimpleName() + " is already registered!");
        registeredClasses.put(clazz.getSimpleName(), clazz);
    }

    @Nullable
    @Override
    public T getNew(String name, IQuesteAPI plugin, Object... data) {
        return getNew(registeredClasses.get(name), plugin, data);
    }

    @Nullable
    @Override
    public abstract T getNew(Class<? extends T> clazz, IQuesteAPI plugin, Object... data);

    @Override
    public abstract String getRegistryName();

    @Override
    public abstract Class<T> getImplementation();

    @Override
    public abstract Material getIcon();
}
