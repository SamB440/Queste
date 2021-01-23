package com.convallyria.queste.managers.data;

import com.convallyria.queste.Queste;
import com.convallyria.queste.managers.data.sql.SqlStorage;
import com.convallyria.queste.managers.data.yml.YamlStorage;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public enum StorageType {
    FILE(YamlStorage.class),
    SQL(SqlStorage.class);

    private final Class<? extends IStorageManager> clazz;

    StorageType(Class<? extends IStorageManager> clazz) {
        this.clazz = clazz;
    }

    public Optional<IStorageManager> get(Queste plugin) {
        plugin.getLogger().info("Loading StorageManager implementation...");
        IStorageManager generatedClazz = null;
        try {
            generatedClazz = clazz.getConstructor(Queste.class).newInstance(plugin);
            plugin.getLogger().info("Loaded StorageManager implementation " + clazz.getName() + ".");
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            plugin.getLogger().severe("Unable to load StorageManager (" + clazz.getName() + ")! Plugin will disable.");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(plugin);
        }

        return Optional.ofNullable(generatedClazz);
    }
}
