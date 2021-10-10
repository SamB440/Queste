package com.convallyria.queste.managers.data;

import co.aikar.idb.DB;
import co.aikar.idb.DbRow;
import com.convallyria.queste.Queste;
import com.convallyria.queste.managers.data.account.QuesteAccount;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.quest.objective.QuestObjective;
import com.convallyria.queste.util.TimeUtils;
import org.bukkit.Bukkit;
import org.intellij.lang.annotations.Language;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public abstract class SQLCommonStorage implements IStorageManager {

    @Language("SQL") protected static final String CREATE_QUESTES_TABLE = "CREATE TABLE IF NOT EXISTS `queste_questes` (" +
            "  `uuid` varchar(32) NOT NULL," +
            "  `questes` BIGINT NOT NULL," +
            "  PRIMARY KEY (`uuid`)" +
            ");";

    @Language("SQL") protected static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `queste_users` (" +
            "  `uuid` varchar(32) NOT NULL," +
            "  `quest` varchar(64) NOT NULL," +
            "  `completed` boolean NOT NULL," +
            "  `start_time` BIGINT NOT NULL," +
            "  `completed_time` BIGINT NOT NULL, " +
            "  PRIMARY KEY (`uuid`, `quest`)" +
            ");";
    @Language("SQL") protected static final String CREATE_OBJECTIVE_TABLE = "CREATE TABLE IF NOT EXISTS `queste_objectives` (" +
            "  `uuid` varchar(32) NOT NULL," +
            "  `quest` varchar(64) NOT NULL," +
            "  `objective` varchar(32) NOT NULL," +
            "  `progress` SMALLINT NOT NULL," +
            "  PRIMARY KEY (`uuid`, `objective`)" +
            ");";
    @Language("SQL") protected static final String SELECT_OBJECTIVE = "SELECT progress, objective FROM queste_objectives WHERE uuid = ? AND quest = ?";
    @Language("SQL") protected static final String INSERT_OBJECTIVE = "INSERT INTO queste_objectives (uuid, quest, objective, progress) VALUES (?, ?, ?, ?)";
    @Language("SQL") protected static final String UPDATE_OBJECTIVE = "UPDATE queste_objectives SET progress = ? WHERE uuid = ? AND objective = ? AND quest = ?";
    @Language("SQL") protected static final String DELETE_OBJECTIVES = "DELETE * FROM queste_objectives WHERE uuid = ?";
    @Language("SQL") protected static final String SELECT_QUEST = "SELECT * FROM queste_users WHERE uuid = ?";
    @Language("SQL") protected static final String INSERT_QUEST = "INSERT INTO queste_users (uuid, quest, completed, start_time, completed_time) VALUES (?, ?, ?, ?, ?)";
    @Language("SQL") protected static final String DELETE_QUESTS = "DELETE * FROM queste_users WHERE uuid = ?";
    @Language("SQL") protected static final String DELETE_QUEST = "DELETE * FROM queste_users WHERE uuid = ? AND quest = ?";
    @Language("SQL") protected static final String DELETE_OBJECTIVE = "DELETE * FROM queste_objectives WHERE uuid = ? AND objective = ? AND quest = ?";
    @Language("SQL") protected static final String DELETE_OBJECTIVE_ALL_QUEST = "DELETE * FROM queste_objectives WHERE uuid = ? AND quest = ?";
    @Language("SQL") protected static final String UPDATE_QUEST = "UPDATE queste_users SET completed = ?, start_time = ?, completed_time = ? WHERE uuid = ? AND quest = ?";

    @Language("SQL") protected static final String SELECT_QUESTES = "SELECT questes FROM queste_questes WHERE uuid = ?";
    @Language("SQL") protected static final String INSERT_QUESTES = "INSERT INTO queste_questes (uuid, questes) VALUES (?, ?)";
    @Language("SQL") protected static final String UPDATE_QUESTES = "UPDATE queste_questes SET questes = ? WHERE uuid = ?";
    @Language("SQL") protected static final String DELETE_QUESTES = "DELETE * FROM queste_questes WHERE uuid = ?";

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

                int startTime = row.getInt("start_time");
                int completeTime = row.getInt("completed_time");
                plugin.debug("Start time is: " + startTime + " Completed time is: " + completeTime);
                // Update objective progress
                try {
                    List<DbRow> objectiveResults = DB.getResults(SELECT_OBJECTIVE, getDatabaseUuid(uuid), quest.getSafeName());
                    Map<UUID, Integer> currentProgress = new HashMap<>();
                    for (DbRow objectiveRow : objectiveResults) {
                        currentProgress.put(fromDatabaseUUID(objectiveRow.getString("objective")), objectiveRow.getInt("progress"));
                    }

                    quest.getObjectives().forEach(objective -> {
                        if (currentProgress.containsKey(objective.getUuid())) {
                            objective.setIncrement(uuid, currentProgress.get(objective.getUuid()));
                        }
                    });
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                boolean completed = row.getInt("completed") == 1;
                if (quest.getTime() != 0) {
                    plugin.debug("Timed quest");
                    long time = TimeUtils.convertTicks(quest.getTime(), TimeUnit.MILLISECONDS);
                    if (System.currentTimeMillis() >= (account.getStartTime(quest) + time)) continue;
                }
                plugin.debug("Adding to cache: " + questName + ", completed? " + completed);
                if (completed) account.addCompletedQuest(quest, completeTime);
                else account.addActiveQuest(quest, startTime);
            }

            try {
                for (DbRow result : DB.getResults(SELECT_QUESTES, getDatabaseUuid(uuid))) {
                    account.setQuestes(result.getInt("questes"));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            cachedAccounts.putIfAbsent(uuid, account);
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
    public CompletableFuture<Void> removeCachedAccount(UUID uuid) {
        QuesteAccount account = cachedAccounts.get(uuid);
        return DB.getResultsAsync(SELECT_QUEST, getDatabaseUuid(uuid)).thenAccept(results -> {
            Map<String, Boolean> current = new HashMap<>();
            for (DbRow row : results) {
                String quest = row.getString("quest");
                boolean completed = row.getInt("completed") == 1;
                plugin.debug("Current: " + quest + ", completed? " + completed);
                current.put(quest, completed);
            }

            try {
                for (Quest allQuest : account.getAllQuests()) {
                    if (!current.containsKey(allQuest.getSafeName())) {
                        if (allQuest.getTime() != 0) {
                            plugin.debug("Timed quest");
                            long time = TimeUtils.convertTicks(allQuest.getTime(), TimeUnit.MILLISECONDS);
                            if (System.currentTimeMillis() >= (account.getStartTime(allQuest) + time)) continue;
                        }
                        plugin.debug("Inserting: " + allQuest.getSafeName() + ", completed? " + allQuest.isCompleted(uuid));
                        DB.executeInsert(INSERT_QUEST, getDatabaseUuid(uuid), allQuest.getSafeName(),
                                allQuest.isCompleted(uuid), account.getStartTime(allQuest), account.getCompletedTime(allQuest));
                    } else {
                        plugin.debug("Updating: " + allQuest.getSafeName() + ", completed? " + allQuest.isCompleted(uuid));
                        if (allQuest.getTime() != 0) {
                            plugin.debug("Remove timed");
                            DB.executeUpdate(DELETE_OBJECTIVE_ALL_QUEST, getDatabaseUuid(uuid), allQuest.getSafeName());
                            DB.executeUpdate(DELETE_QUEST, getDatabaseUuid(uuid), allQuest.getSafeName());
                        } else {
                            DB.executeUpdate(UPDATE_QUEST, allQuest.isCompleted(uuid), account.getStartTime(allQuest),
                                    account.getCompletedTime(allQuest), getDatabaseUuid(uuid), allQuest.getSafeName());
                        }
                    }

                    Map<UUID, Integer> currentProgress = new HashMap<>();
                    for (DbRow row : DB.getResults(SELECT_OBJECTIVE, getDatabaseUuid(uuid), allQuest.getSafeName())) {
                        currentProgress.put(fromDatabaseUUID(row.getString("objective")), row.getInt("progress"));
                    }

                    for (QuestObjective objective : allQuest.getObjectives()) {
                        int progress = objective.getIncrement(uuid);
                        if (!currentProgress.containsKey(objective.getUuid())) {
                            DB.executeInsert(INSERT_OBJECTIVE, getDatabaseUuid(uuid), allQuest.getSafeName(),
                                    getDatabaseUuid(objective.getUuid()), progress);
                        } else {
                            DB.executeUpdate(UPDATE_OBJECTIVE, progress, getDatabaseUuid(uuid),
                                    getDatabaseUuid(objective.getUuid()), allQuest.getSafeName());
                        }
                        objective.untrack(uuid);
                    }
                }

                List<DbRow> rows = DB.getResults(SELECT_QUESTES, getDatabaseUuid(uuid));
                if (rows.isEmpty()) {
                    DB.executeInsert(INSERT_QUESTES, getDatabaseUuid(uuid), account.getQuestes());
                } else {
                    DB.executeUpdate(UPDATE_QUESTES, account.getQuestes(), getDatabaseUuid(uuid));
                }
                cachedAccounts.remove(uuid);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).exceptionally(t -> {
            t.printStackTrace();
            return null;
        });
    }
}
