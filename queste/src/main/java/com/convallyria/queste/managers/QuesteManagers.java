package com.convallyria.queste.managers;

import com.convallyria.queste.Queste;
import com.convallyria.queste.managers.data.IStorageManager;
import com.convallyria.queste.managers.data.QuesteCache;
import com.convallyria.queste.managers.data.StorageType;
import com.convallyria.queste.managers.registry.QuesteRegistry;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.quest.objective.QuestObjective;
import com.convallyria.queste.quest.objective.QuestObjectiveRegistry;
import com.convallyria.queste.quest.requirement.QuestRequirementRegistry;
import com.convallyria.queste.quest.reward.QuestRewardRegistry;
import hu.trigary.advancementcreator.Advancement;
import hu.trigary.advancementcreator.shared.ItemObject;
import hu.trigary.advancementcreator.trigger.ImpossibleTrigger;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class QuesteManagers implements IQuesteManagers {

    private final Queste plugin;

    private IStorageManager storageManager;
    private final QuesteCache questeCache;
    private final Map<Class<? extends QuesteRegistry<?>>, QuesteRegistry<?>> registry;

    public File getPresetFolder() {
        return new File(plugin.getDataFolder() + File.separator + "presets");
    }

    public File getPresetFolder(QuesteRegistry<?> questeRegistry) {
        return new File(plugin.getDataFolder() + File.separator + "presets" + File.separator + questeRegistry.getRegistryName());
    }

    public QuesteManagers(Queste plugin) {
        this.plugin = plugin;
        StorageType.valueOf(plugin.getConfig().getString("settings.storage.mode").toUpperCase())
            .get(plugin)
            .ifPresent(generatedManager -> storageManager = generatedManager);
        if (storageManager == null) throw new IllegalStateException("Could not find StorageManager!");

        this.questeCache = new QuesteCache(plugin);
        this.registry = new ConcurrentHashMap<>();
        registry.put(QuestObjectiveRegistry.class, new QuestObjectiveRegistry());
        registry.put(QuestRewardRegistry.class, new QuestRewardRegistry());
        registry.put(QuestRequirementRegistry.class, new QuestRequirementRegistry());

        File accountsFolder = new File(plugin.getDataFolder() + "/accounts/");
        if (!accountsFolder.exists()) accountsFolder.mkdirs();

        registry.values().forEach(register -> {
            File presetRegistryFolder = getPresetFolder(register);
            if (!presetRegistryFolder.exists()) presetRegistryFolder.mkdirs();
        });
        
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
                reader.close();
                questeCache.addQuest(quest);
                long startTime = System.currentTimeMillis();
                if (plugin.getConfig().getBoolean("settings.server.advancements.generate")) {
                    this.loadAdvancements(quest, plugin);
                }
                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                if (plugin.debug()) {
                    plugin.getLogger().info("Loaded quest " + quest.getName() + ".");
                }
                plugin.getLogger().info("Done in " + totalTime + "ms.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Bukkit.reloadData();
        } catch (Exception ignored) { }
    }

    private void loadAdvancements(Quest quest, Queste plugin) {
        plugin.getLogger().info("Generating advancements, this could take a while...");
        new Advancement(quest.getKey(), new ItemObject().setItem(Material.TOTEM_OF_UNDYING),
                new TextComponent(quest.getDisplayName()), new TextComponent(""))
                .addTrigger("impossible", new ImpossibleTrigger())
                .setAnnounce(false)
                .setToast(true)
                .makeRoot("block/dirt", false)
                .setFrame(Advancement.Frame.CHALLENGE)
                .activate(false);
        SortedMap<Integer, QuestObjective> sortedMap = new TreeMap<>();

        for (QuestObjective objective : quest.getObjectives()) {
            sortedMap.put(objective.getStoryModeKey(), objective);
        }

        if (plugin.debug()) {
            plugin.getLogger().info("Data sorted: ");
            plugin.getLogger().info(sortedMap.toString());
        }

        QuestObjective previousObjective = null;
        for (Integer key : sortedMap.keySet()) {
            NamespacedKey namespacedKey = previousObjective == null ? quest.getKey() : previousObjective.getKey();
            QuestObjective objective = previousObjective = sortedMap.get(key);
            new Advancement(objective.getKey(), new ItemObject().setItem(Material.TOTEM_OF_UNDYING),
                    new TextComponent(objective.getDisplayName()), new TextComponent(""))
                    .addTrigger("impossible", new ImpossibleTrigger())
                    .setAnnounce(false)
                    .setToast(true)
                    .makeChild(namespacedKey)
                    .setFrame(Advancement.Frame.GOAL)
                    .activate(false);
        }
    }

    @Override
    public IStorageManager getStorageManager() {
        return storageManager;
    }

    @Override
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
