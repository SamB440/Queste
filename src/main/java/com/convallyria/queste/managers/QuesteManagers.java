package com.convallyria.queste.managers;

import com.convallyria.queste.Queste;
import com.convallyria.queste.managers.data.QuesteCache;
import com.convallyria.queste.managers.data.StorageManager;
import com.convallyria.queste.managers.data.StorageType;
import com.convallyria.queste.quest.Quest;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

public class QuesteManagers {

    private StorageManager storageManager;
    private QuesteCache questeCache;

    public QuesteManagers(Queste plugin) {
        StorageType.valueOf(plugin.getConfig().getString("settings.storage.mode").toUpperCase())
            .get(plugin)
            .ifPresent(storageManager1 -> storageManager = storageManager1);
        if (storageManager == null) throw new IllegalStateException("Could not find StorageManager!");

        this.questeCache = new QuesteCache(plugin);

        File folder = new File(plugin.getDataFolder() + "/quests/");
        if (!folder.exists()) folder.mkdirs();
        for (File file : folder.listFiles()) {
            try {
                Reader reader = new FileReader(file);
                Quest quest = plugin.getGson().fromJson(reader, Quest.class);
                quest.getObjectives().forEach(questObjective -> {
                    Bukkit.getPluginManager().registerEvents(questObjective, plugin);
                });
                if (plugin.debug()) {
                    plugin.getLogger().info("Loaded quest " + quest.getName() + ".");
                }
                questeCache.addQuest(quest);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public QuesteCache getQuesteCache() {
        return questeCache;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }
}
