package com.convallyria.queste.managers.data.sql;

import co.aikar.idb.DB;
import co.aikar.idb.Database;
import co.aikar.idb.DatabaseOptions;
import co.aikar.idb.DbRow;
import co.aikar.idb.PooledDatabaseOptions;
import com.convallyria.queste.Queste;
import com.convallyria.queste.managers.data.IStorageManager;
import com.convallyria.queste.managers.data.account.QuesteAccount;
import com.convallyria.queste.quest.Quest;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SqlStorage implements IStorageManager {

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `queste_users` (" +
            "  `uuid` varchar(32)," +
            "  `quest` varchar(64)," +
            "  `active` boolean," +
            "  PRIMARY KEY (`uuid`, `quest`)" +
            ");";
    private static final String SELECT_QUEST = "SELECT * FROM queste_users WHERE uuid = ?";
    private static final String INSERT_QUEST = "INSERT INTO queste_users (uuid, quest, active) VALUES (?, ?, ?)";
    private static final String DELETE_QUESTS = "DELETE * FROM queste_users WHERE uuid = ?";
    private static final String DELETE_QUEST = "DELETE * FROM queste_users WHERE uuid = ? AND quest = ?";
    private static final String UPDATE_QUEST = "UPDATE queste_users SET active = ? WHERE uuid = ? AND quest = ?";
    
    private final Queste plugin;
    private final ConcurrentMap<UUID, QuesteAccount> cachedAccounts = new ConcurrentHashMap<>();

    public SqlStorage(Queste plugin) {
        this.plugin = plugin;

        DatabaseOptions options = DatabaseOptions.builder().mysql(plugin.getConfig().getString("settings.sql.user"),
                plugin.getConfig().getString("settings.sql.pass"),
                plugin.getConfig().getString("settings.sql.db"),
                plugin.getConfig().getString("settings.sql.host") + ":" + plugin.getConfig().getString("settings.sql.port")).build();
        Database db = PooledDatabaseOptions.builder().options(options).createHikariDatabase();
        DB.setGlobalDatabase(db);
        try {
            db.executeUpdate(CREATE_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompletableFuture<QuesteAccount> getAccount(UUID uuid) {
        // Add a check to ensure accounts aren't taking a long time
        long startTime = System.currentTimeMillis();
        CompletableFuture<QuesteAccount> future = new CompletableFuture<>();
        if (cachedAccounts.containsKey(uuid)) {
            future.complete(cachedAccounts.get(uuid));
        } else {
            DB.getResultsAsync(SELECT_QUEST, getDatabaseUuid(uuid)).thenAccept(results -> {
                Map<Quest, Boolean> quests = new HashMap<>();
                for (DbRow row : results) {
                    String questName = row.getString("quest");
                    Quest quest = plugin.getManagers().getQuesteCache().getQuest(row.getString(questName));
                    if (quest == null) {
                        plugin.getLogger().warning("Cannot find quest " + questName);
                        continue;
                    }
                    
                    boolean completed = row.get("completed");
                    quests.put(quest, completed);
                }
                
                QuesteAccount account = new QuesteAccount(uuid);
                quests.forEach((quest, completed) -> {
                    if (completed) account.addCompletedQuest(quest);
                    else account.addActiveQuest(quest);
                });
                cachedAccounts.put(uuid, account);
                future.complete(account);
                
                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                if (totalTime >= 20) {
                    plugin.getLogger().warning("Grabbing accounts is taking a long time! (" + totalTime + "ms)");
                }
            }).exceptionally(t -> {
                t.printStackTrace();
                return null;
            });
        }
        return future;
    }

    @Override
    public ConcurrentMap<UUID, QuesteAccount> getCachedAccounts() {
        return cachedAccounts;
    }

    @Override
    public void deleteAccount(UUID uuid) {
        cachedAccounts.remove(uuid);
        DB.executeUpdateAsync(DELETE_QUESTS, getDatabaseUuid(uuid));
    }

    @Override
    public void removeCachedAccount(UUID uuid) {
        QuesteAccount account = cachedAccounts.get(uuid);
        DB.getResultsAsync(SELECT_QUEST, getDatabaseUuid(uuid)).thenAccept(results -> {
            Map<String, Boolean> current = new HashMap<>();
            for (DbRow row : results) {
                current.put(row.getString("quest"), row.get("completed"));
            }
            
            for (Quest completedQuest : account.getCompletedQuests()) {
                if (!current.containsKey(completedQuest.getName())) {
                    try {
                        DB.executeInsert(INSERT_QUEST, getDatabaseUuid(uuid), completedQuest.getName(), true);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                } else {
                    if (!current.get(completedQuest.getName())) {
                        DB.executeUpdateAsync(UPDATE_QUEST, true, getDatabaseUuid(uuid), completedQuest.getName());
                    }
                }
            }
    
            for (Quest activeQuest : account.getActiveQuests()) {
                if (!current.containsKey(activeQuest.getName())) {
                    try {
                        DB.executeInsert(INSERT_QUEST, getDatabaseUuid(uuid), activeQuest.getName(), false);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                } else {
                    if (current.containsKey(activeQuest.getName())) {
                        DB.executeUpdateAsync(UPDATE_QUEST, true, getDatabaseUuid(uuid), activeQuest.getName());
                    }
                }
            }
            cachedAccounts.remove(uuid);
        }).exceptionally(t -> {
            t.printStackTrace();
            return null;
        });
    }
}
