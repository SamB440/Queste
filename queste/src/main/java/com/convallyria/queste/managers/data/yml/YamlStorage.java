package com.convallyria.queste.managers.data.yml;

import com.convallyria.queste.Queste;
import com.convallyria.queste.managers.data.IStorageManager;
import com.convallyria.queste.managers.data.account.QuesteAccount;
import com.convallyria.queste.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class YamlStorage implements IStorageManager {

    private final ConcurrentMap<UUID, QuesteAccount> cachedAccounts;

    private final Queste plugin;

    public YamlStorage(Queste plugin) {
        this.plugin = plugin;
        this.cachedAccounts = new ConcurrentHashMap<>();
    }

    @Override
    public CompletableFuture<QuesteAccount> getAccount(UUID uuid) {
        CompletableFuture<QuesteAccount> future = new CompletableFuture<>();
        if (cachedAccounts.containsKey(uuid)) {
            future.complete(cachedAccounts.get(uuid));
        } else {
            Player player = Bukkit.getPlayer(uuid);
            File file = new File(plugin.getDataFolder() + "/accounts/" + uuid + ".yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            QuesteAccount account = new QuesteAccount(uuid);
            for (String activeQuest : config.getStringList("Quests")) {
                if (plugin.getManagers().getQuesteCache().getQuests().containsKey(activeQuest)) {
                    Quest quest = plugin.getManagers().getQuesteCache().getQuests().get(activeQuest);
                    quest.getObjectives().forEach(objective -> {
                        int progress = config.getInt(quest.getName() + "." + objective.getSafeName() + "." + uuid);
                        objective.setIncrement(player, progress);
                    });
                    long time = config.getLong(quest.getName() + "." + "startTime" + "." + uuid);
                    account.addActiveQuest(quest, time);
                } else {
                    plugin.getLogger().warning(activeQuest + " quest not found.");
                }
            }

            for (String completedQuest : config.getStringList("CompletedQuests")) {
                if (plugin.getManagers().getQuesteCache().getQuests().containsKey(completedQuest)) {
                    Quest quest = plugin.getManagers().getQuesteCache().getQuests().get(completedQuest);
                    quest.getObjectives().forEach(objective -> {
                        int progress = config.getInt(quest.getName() + "." + objective.getSafeName() + "." + uuid);
                        objective.setIncrement(player, progress);
                    });
                    account.addCompletedQuest(quest);
                } else {
                    plugin.getLogger().warning(completedQuest + " quest not found.");
                }
            }

            account.setQuestes(config.getInt("Questes"));
            cachedAccounts.putIfAbsent(uuid, account);
            future.complete(account);
        }
        return future;
    }

    @Override
    public ConcurrentMap<UUID, QuesteAccount> getCachedAccounts() {
        return cachedAccounts;
    }

    @Override
    public void deleteAccount(UUID uuid) {
        File file = new File(plugin.getDataFolder() + "/accounts/" + uuid.toString() + ".yml");
        try {
            if (!Files.deleteIfExists(file.toPath())) {
                plugin.getLogger().warning("Could not delete file " + file.toPath() + ".");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        cachedAccounts.remove(uuid);
    }

    @Override
    public CompletableFuture<Void> removeCachedAccount(UUID uuid) {
        QuesteAccount account = cachedAccounts.get(uuid);
        File file = new File(plugin.getDataFolder() + "/accounts/" + uuid.toString() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        List<String> activeQuests = new ArrayList<>();
        account.getActiveQuests().forEach(quest -> {
            activeQuests.add(quest.getName());
            config.set(quest.getName() + "." + "startTime" + "." + uuid, account.getStartTime(quest));
        });

        List<String> completedQuests = new ArrayList<>();
        account.getCompletedQuests().forEach(completedQuest -> completedQuests.add(completedQuest.getName()));

        config.set("Quests", activeQuests);
        config.set("CompletedQuests", completedQuests);

        account.getAllQuests().forEach(quest -> {
            quest.getObjectives().forEach(objective -> {
                Player player = Bukkit.getPlayer(uuid);
                int progress = objective.getIncrement(player);
                config.set(quest.getName() + "." + objective.getSafeName() + "." + uuid, progress);
                objective.untrack(uuid);
            });
        });

        config.set("Questes", account.getQuestes());
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cachedAccounts.remove(uuid);
        return CompletableFuture.completedFuture(null);
    }
}
