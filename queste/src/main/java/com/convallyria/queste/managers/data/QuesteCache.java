package com.convallyria.queste.managers.data;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QuesteCache implements IQuesteCache {

    private final Queste plugin;
    private final Map<String, Quest> quests;

    public QuesteCache(Queste plugin) {
        this.plugin = plugin;
        this.quests = new ConcurrentHashMap<>();
    }

    @Override
    public Map<String, Quest> getQuests() {
        return quests;
    }

    @Override
    public void reload() {
        quests.clear();
        File folder = new File(plugin.getDataFolder() + "/quests/");
        if (!folder.exists()) folder.mkdirs();
        for (File file : folder.listFiles()) {
            try {
                Quest quest;
                try (Reader reader = new FileReader(file)) {
                    quest = plugin.getGson().fromJson(reader, Quest.class);
                    if (quest == null) continue;
                }
                quest.getObjectives().forEach(questObjective -> {
                    Bukkit.getPluginManager().registerEvents(questObjective, plugin);
                });
                if (plugin.debug()) {
                    plugin.getLogger().info("Loaded quest " + quest.getName() + ".");
                }
                addQuest(quest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    @Nullable
    public Quest getQuest(String name) {
        return quests.get(name);
    }

    @Override
    public void addQuest(Quest quest) {
        quests.put(quest.getName(), quest);
    }

    @Override
    public void removeQuest(Quest quest) {
        quests.remove(quest.getName());
    }
}
