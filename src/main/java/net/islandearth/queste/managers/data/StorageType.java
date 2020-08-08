package net.islandearth.queste.managers.data;

import net.islandearth.queste.Queste;
import net.islandearth.queste.managers.data.sql.SqlStorage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public enum StorageType {
	SQL(SqlStorage.class);

	private final Class<? extends StorageManager> clazz;

	StorageType(Class<? extends StorageManager> clazz) {
		this.clazz = clazz;
	}

	public Optional<StorageManager> get() {
		Queste plugin = JavaPlugin.getPlugin(Queste.class);
		plugin.getLogger().info("Loading StorageManager implementation...");
		StorageManager generatedClazz = null;
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
