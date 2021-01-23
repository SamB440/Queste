package com.convallyria.queste.managers.data.yml;

import com.convallyria.queste.Queste;
import com.convallyria.queste.managers.data.IStorageManager;
import com.convallyria.queste.managers.data.account.QuesteAccount;
import com.convallyria.queste.quest.Quest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

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
            File file = new File(plugin.getDataFolder() + "/accounts/" + uuid.toString() + ".yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            List<Quest> activeQuests = new ArrayList<>();
            for (String results : config.getStringList("Quests")) {
                if (plugin.getManagers().getQuesteCache().getQuests().containsKey(results)) {
                    activeQuests.add(plugin.getManagers().getQuesteCache().getQuests().get(results));
                } else {
                    plugin.getLogger().warning(results + " quest not found.");
                }
            }

            List<Quest> completedQuests = new ArrayList<>();
            for (String completedQuest : config.getStringList("CompletedQuests")) {
                if (plugin.getManagers().getQuesteCache().getQuests().containsKey(completedQuest)) {
                    completedQuests.add(plugin.getManagers().getQuesteCache().getQuests().get(completedQuest));
                } else {
                    plugin.getLogger().warning(completedQuest + " quest not found.");
                }
            }

            QuesteAccount account = new QuesteAccount(uuid);
            activeQuests.forEach(account::addActiveQuest);
            completedQuests.forEach(account::addCompletedQuest);
            cachedAccounts.put(uuid, account);
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
                plugin.getLogger().warning("Could not delete file " + file.toPath().toString() + ".");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        cachedAccounts.remove(uuid);
    }

    @Override
    public void removeCachedAccount(UUID uuid) {
        QuesteAccount account = cachedAccounts.get(uuid);
        File file = new File(plugin.getDataFolder() + "/accounts/" + uuid.toString() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        List<String> newData = new ArrayList<>();
        account.getActiveQuests().forEach(quest -> newData.add(quest.getName()));

        List<String> completedQuests = new ArrayList<>();
        account.getCompletedQuests().forEach(completedQuest -> completedQuests.add(completedQuest.getName()));

        config.set("Quests", newData);
        config.set("CompletedQuests", completedQuests);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cachedAccounts.remove(uuid);
    }
}
