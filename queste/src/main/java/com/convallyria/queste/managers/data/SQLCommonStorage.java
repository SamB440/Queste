package com.convallyria.queste.managers.data;

import co.aikar.idb.DB;
import co.aikar.idb.DbRow;
import com.convallyria.queste.Queste;
import com.convallyria.queste.managers.data.account.QuesteAccount;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.quest.objective.QuestObjective;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;

public abstract class SQLCommonStorage implements IStorageManager {

    protected static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `queste_users` (" +
            "  `uuid` varchar(32) NOT NULL," +
            "  `quest` varchar(64) NOT NULL," +
            "  `completed` boolean NOT NULL," +
            "  `start_time` BIGINT NOT NULL," +
            "  `completed_time` BIGINT NOT NULL, " +
            "  PRIMARY KEY (`uuid`, `quest`)" +
            ");";
    protected static final String CREATE_OBJECTIVE_TABLE = "CREATE TABLE IF NOT EXISTS `queste_objectives` (" +
            "  `uuid` varchar(32) NOT NULL," +
            "  `objective` varchar(64) NOT NULL," +
            "  `progress` SMALLINT NOT NULL," +
            "  PRIMARY KEY (`uuid`, `objective`)" +
            ");";
    protected static final String SELECT_OBJECTIVE = "SELECT progress, objective FROM queste_objectives WHERE uuid = ?";
    protected static final String INSERT_OBJECTIVE = "INSERT INTO queste_objectives (uuid, objective, progress) VALUES (?, ?, ?)";
    protected static final String UPDATE_OBJECTIVE = "UPDATE queste_objectives SET progress = ? WHERE uuid = ? AND objective = ?";
    protected static final String DELETE_OBJECTIVES = "DELETE * FROM queste_objectives WHERE uuid = ?";
    protected static final String SELECT_QUEST = "SELECT * FROM queste_users WHERE uuid = ?";
    protected static final String INSERT_QUEST = "INSERT INTO queste_users (uuid, quest, completed, start_time, completed_time) VALUES (?, ?, ?, ?, ?)";
    protected static final String DELETE_QUESTS = "DELETE * FROM queste_users WHERE uuid = ?";
    protected static final String DELETE_QUEST = "DELETE * FROM queste_users WHERE uuid = ? AND quest = ?";
    protected static final String UPDATE_QUEST = "UPDATE queste_users SET completed = ?, start_time = ?, completed_time = ? WHERE uuid = ? AND quest = ?";

    protected final Queste plugin;
    protected final ConcurrentMap<UUID, QuesteAccount> cachedAccounts = new ConcurrentHashMap<>();

    protected SQLCommonStorage(Queste plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<QuesteAccount> getAccount(UUID uuid) {
        CompletableFuture<QuesteAccount> future = new CompletableFuture<>();
        if (cachedAccounts.containsKey(uuid)) {
            future.complete(cachedAccounts.get(uuid));
            return future;
        }

        DB.getResultsAsync(SELECT_QUEST, getDatabaseUuid(uuid)).thenAccept(results -> {
            QuesteAccount account = new QuesteAccount(uuid);
            plugin.debug("Query results: " + results);
            for (DbRow row : results) {
                String questName = row.getString("quest");
                plugin.debug("Current row: " + questName);
                Quest quest = plugin.getManagers().getQuesteCache().getQuest(questName);
                if (quest == null) {
                    plugin.debug("Cannot find quest " + questName, Level.WARNING);
                    continue;
                }

                long startTime = row.getLong("start_time");
                long completeTime = row.getInt("completed_time");
                plugin.debug("Start time is: " + startTime + " Completed time is: " + completeTime);
                // Update objective progress
                DB.getResultsAsync(SELECT_OBJECTIVE,  getDatabaseUuid(uuid)).thenAccept(objectiveResults -> {
                    Map<String, Integer> currentProgress = new HashMap<>();
                    for (DbRow objectiveRow : objectiveResults) {
                        currentProgress.put(objectiveRow.getString("objective"), objectiveRow.getInt("progress"));
                    }

                    quest.getObjectives().forEach(objective -> {
                        if (currentProgress.containsKey(objective.getSafeName())) {
                            Player player = Bukkit.getPlayer(uuid);
                            objective.setIncrement(player, currentProgress.get(objective.getSafeName()));
                        }
                    });
                });

                boolean completed = row.getInt("completed") == 1;
                plugin.debug("Adding to cache: " + questName + ", completed? " + completed);
                if (completed) account.addCompletedQuest(quest, completeTime);
                else account.addActiveQuest(quest, startTime);
            }

            cachedAccounts.put(uuid, account);
            Bukkit.getScheduler().runTask(plugin, () -> future.complete(account)); // Enforce main thread completion
        }).exceptionally(t -> {
            t.printStackTrace();
            return null;
        });

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
        DB.executeUpdateAsync(DELETE_OBJECTIVES, getDatabaseUuid(uuid));
    }

    @Override
    public void removeCachedAccount(UUID uuid) {
        QuesteAccount account = cachedAccounts.get(uuid);
        Player player = Bukkit.getPlayer(uuid);
        DB.getResultsAsync(SELECT_QUEST, getDatabaseUuid(uuid)).thenAccept(results -> {
            Map<String, Boolean> current = new HashMap<>();
            for (DbRow row : results) {
                String quest = row.getString("quest");
                boolean completed = row.getInt("completed") == 1;
                plugin.debug("Current: " + quest + ", completed? " + completed);
                current.put(quest, completed);
            }

            try {
                for (Quest allQuest : account.getAllQuests()) {
                    if (!current.containsKey(allQuest.getName())) {
                        plugin.debug("Inserting: " + allQuest.getName() + ", completed? " + allQuest.isCompleted(player));
                        DB.executeInsert(INSERT_QUEST, getDatabaseUuid(uuid), allQuest.getName(),
                                allQuest.isCompleted(player), account.getStartTime(allQuest), account.getCompletedTime(allQuest));
                        continue;
                    }

                    plugin.debug("Updating: " + allQuest.getName() + ", completed? " + allQuest.isCompleted(player));
                    if (plugin.isShuttingDown()) {
                        DB.executeUpdate(UPDATE_QUEST, allQuest.isCompleted(player), account.getStartTime(allQuest),
                                account.getCompletedTime(allQuest), getDatabaseUuid(uuid), allQuest.getName());
                    } else {
                        DB.executeUpdateAsync(UPDATE_QUEST, allQuest.isCompleted(player), account.getStartTime(allQuest),
                                account.getCompletedTime(allQuest), getDatabaseUuid(uuid), allQuest.getName());
                    }
                }
                
                CompletableFuture<List<DbRow>> future = plugin.isShuttingDown()
                        ? CompletableFuture.completedFuture(DB.getResults(SELECT_OBJECTIVE, getDatabaseUuid(uuid)))
                        : DB.getResultsAsync(SELECT_OBJECTIVE, getDatabaseUuid(uuid));
                future.thenAccept(objectiveResults -> {
                    Map<String, Integer> currentProgress = new HashMap<>();
                    for (DbRow row : objectiveResults) {
                        currentProgress.put(row.getString("objective"), row.getInt("progress"));
                    }
        
                    for (Quest quest : account.getAllQuests()) {
                        for (QuestObjective objective : quest.getObjectives()) {
                            int progress = objective.getIncrement(player);
                            if (!currentProgress.containsKey(objective.getSafeName())) {
                                try {
                                    DB.executeInsert(INSERT_OBJECTIVE, getDatabaseUuid(uuid), objective.getSafeName(), progress);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                DB.executeUpdateAsync(UPDATE_OBJECTIVE, progress, getDatabaseUuid(uuid), objective.getSafeName());
                            }
                            objective.untrack(uuid);
                        }
                    }
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
            cachedAccounts.remove(uuid);
        }).exceptionally(t -> {
            t.printStackTrace();
            return null;
        });
    }
}
