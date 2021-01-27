package com.convallyria.queste.managers.registry;

import com.convallyria.queste.Queste;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
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

public abstract class QuesteRegistry<T> {

    private final Map<String, Class<? extends T>> registeredClasses;

    protected QuesteRegistry() {
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

    @NotNull
    public List<String> loadAllPresets() {
        List<String> names = new ArrayList<>();
        Queste plugin = JavaPlugin.getPlugin(Queste.class);
        File preset = new File(plugin.getManagers().getPresetFolder(this) + File.separator + "objectives" + File.separator);
        if (!preset.exists()) return names;
        for (File file : preset.listFiles()) {
            if (!file.getName().endsWith(".json")) continue;
            names.add(file.getName().replace(".json", ""));
        }
        return names;
    }

    public void savePreset(Object toSave, String name) {
        Queste plugin = JavaPlugin.getPlugin(Queste.class);
        File preset = new File(plugin.getManagers().getPresetFolder(this) + File.separator + "objectives" + File.separator + name + ".json");
        try {
            Writer writer = new FileWriter(preset);
            Gson gson = plugin.getGson();
            gson.toJson(toSave, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public T loadPreset(String name) {
        Queste plugin = JavaPlugin.getPlugin(Queste.class);
        File preset = new File(plugin.getManagers().getPresetFolder(this) + File.separator + "objectives" + File.separator + name + ".json");
        if (!preset.exists()) return null;
        try {
            FileReader reader = new FileReader(preset);
            T implementedClass = plugin.getGson().fromJson(reader, getImplementation());
            reader.close();
            return implementedClass;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Attempts to register a class.
     * @param clazz class to register
     * @throws IllegalArgumentException if class is already registered
     */
    public void register(Class<? extends T> clazz) {
        if (registeredClasses.containsKey(clazz.getSimpleName()))
            throw new IllegalArgumentException(clazz.getSimpleName() + " is already registered!");
        registeredClasses.put(clazz.getSimpleName(), clazz);
    }

    @Nullable
    public T getNew(String name, Queste plugin) {
        return getNew(registeredClasses.get(name), plugin);
    }

    @Nullable
    public abstract T getNew(Class<? extends T> clazz, Queste plugin);

    public abstract String getRegistryName();

    public abstract Class<T> getImplementation();
}
