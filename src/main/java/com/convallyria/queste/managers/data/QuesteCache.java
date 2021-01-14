package com.convallyria.queste.managers.data;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QuesteCache {

    private final Queste plugin;
    private final Map<String, Quest> quests;

    public QuesteCache(Queste plugin) {
        this.plugin = plugin;
        this.quests = new ConcurrentHashMap<>();
    }

    public ImmutableMap<String, Quest> getQuests() {
        return ImmutableMap.copyOf(quests);
    }

    public void reload() {
        quests.clear();
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
                addQuest(quest);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    public Quest getQuest(String name) {
        return quests.get(name);
    }

    public void addQuest(Quest quest) {
        quests.put(quest.getName(), quest);
    }

    public void removeQuest(Quest quest) {
        quests.remove(quest.getName());
    }
}
