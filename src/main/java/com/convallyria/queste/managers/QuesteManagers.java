package com.convallyria.queste.managers;

import com.convallyria.queste.Queste;
import com.convallyria.queste.managers.data.QuesteCache;
import com.convallyria.queste.managers.data.StorageManager;
import com.convallyria.queste.managers.data.StorageType;
import com.convallyria.queste.managers.registry.QuesteRegistry;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.quest.objective.QuestObjective;
import com.convallyria.queste.quest.objective.QuestObjectiveRegistry;
import com.convallyria.queste.quest.reward.QuestRewardRegistry;
import com.convallyria.queste.quest.start.QuestRequirementRegistry;
import hu.trigary.advancementcreator.Advancement;
import hu.trigary.advancementcreator.shared.ItemObject;
import hu.trigary.advancementcreator.trigger.ImpossibleTrigger;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QuesteManagers {

    private StorageManager storageManager;
    private final QuesteCache questeCache;
    private final Map<Class<? extends QuesteRegistry<?>>, QuesteRegistry<?>> registry;

    public QuesteManagers(Queste plugin) {
        StorageType.valueOf(plugin.getConfig().getString("settings.storage.mode").toUpperCase())
            .get(plugin)
            .ifPresent(storageManager1 -> storageManager = storageManager1);
        if (storageManager == null) throw new IllegalStateException("Could not find StorageManager!");

        this.questeCache = new QuesteCache(plugin);
        this.registry = new ConcurrentHashMap<>();
        registry.put(QuestObjectiveRegistry.class, new QuestObjectiveRegistry());
        registry.put(QuestRewardRegistry.class, new QuestRewardRegistry());
        registry.put(QuestRequirementRegistry.class, new QuestRequirementRegistry());

        File accountsFolder = new File(plugin.getDataFolder() + "/accounts/");
        if (!accountsFolder.exists()) accountsFolder.mkdirs();
        
        File folder = new File(plugin.getDataFolder() + "/quests/");
        if (!folder.exists()) folder.mkdirs();
        for (File file : folder.listFiles()) {
            try {
                Reader reader = new FileReader(file);
                Quest quest = plugin.getGson().fromJson(reader, Quest.class);
                if (quest == null) {
                    plugin.getLogger().severe("Unable to load quest " + file.getName() + ". Json invalid.");
                    continue;
                }
                quest.getObjectives().forEach(questObjective -> {
                    if (questObjective.getPluginRequirement() != null
                        && Bukkit.getPluginManager().getPlugin(questObjective.getPluginRequirement()) == null) {
                        plugin.getLogger().warning("Objective " + questObjective.getName() + " requires plugin "
                                + questObjective.getPluginRequirement()
                                + " which is not loaded. Objective will be skipped for event registration.");
                        return;
                    }
                    Bukkit.getPluginManager().registerEvents(questObjective, plugin);
                });
                if (plugin.debug()) {
                    plugin.getLogger().info("Loaded quest " + quest.getName() + ".");
                }
                questeCache.addQuest(quest);
                new Advancement(quest.getKey(), new ItemObject().setItem(Material.TOTEM_OF_UNDYING),
                        new TextComponent(quest.getDisplayName()), new TextComponent(""))
                        .addTrigger("impossible", new ImpossibleTrigger())
                        .setAnnounce(false)
                        .setToast(true)
                        .makeRoot("block/dirt", false)
                        .setFrame(Advancement.Frame.CHALLENGE)
                        .activate(true);
                for (QuestObjective objective : quest.getObjectives()) {
                    new Advancement(objective.getKey(), new ItemObject().setItem(Material.TOTEM_OF_UNDYING),
                            new TextComponent(objective.getDisplayName()), new TextComponent(""))
                            .addTrigger("impossible", new ImpossibleTrigger())
                            .setAnnounce(false)
                            .setToast(true)
                            .makeChild(quest.getKey())
                            .setFrame(Advancement.Frame.GOAL)
                            .activate(true);
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public QuesteCache getQuesteCache() {
        return questeCache;
    }

    public Map<Class<? extends QuesteRegistry<?>>, QuesteRegistry<?>> getQuestRegistry() {
        return registry;
    }

    @Nullable
    public QuesteRegistry<?> getQuestRegistry(Class<? extends QuesteRegistry<?>> clazz) {
        return registry.get(clazz);
    }
}
