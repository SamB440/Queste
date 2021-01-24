package com.convallyria.queste.managers.data;

import co.aikar.idb.DB;
import co.aikar.idb.DbRow;
import com.convallyria.queste.Queste;
import com.convallyria.queste.managers.data.account.QuesteAccount;
import com.convallyria.queste.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class SQLCommonStorage implements IStorageManager {

    protected static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `queste_users` (" +
            "  `uuid` varchar(32)," +
            "  `quest` varchar(64)," +
            "  `completed` boolean," +
            "  PRIMARY KEY (`uuid`, `quest`)" +
            ");";
    protected static final String CREATE_OBJECTIVE_TABLE = "CREATE TABLE IF NOT EXISTS `queste_objectives` (" +
            "  `uuid` varchar(32)," +
            "  `objective` varchar(64)," +
            "  `progress` SMALLINT," +
            "  PRIMARY KEY (`uuid`, `objective`)" +
            ");";
    protected static final String SELECT_OBJECTIVE = "SELECT progress, objective FROM queste_objectives WHERE uuid = ?";
    protected static final String INSERT_OBJECTIVE = "INSERT INTO queste_objectives (uuid, objective, progress) VALUES (?, ?, ?)";
    protected static final String UPDATE_OBJECTIVE = "UPDATE queste_objectives SET progress = ? WHERE uuid = ? AND objective = ?";
    protected static final String SELECT_QUEST = "SELECT * FROM queste_users WHERE uuid = ?";
    protected static final String INSERT_QUEST = "INSERT INTO queste_users (uuid, quest, completed) VALUES (?, ?, ?)";
    protected static final String DELETE_QUESTS = "DELETE * FROM queste_users WHERE uuid = ?";
    protected static final String DELETE_QUEST = "DELETE * FROM queste_users WHERE uuid = ? AND quest = ?";
    protected static final String UPDATE_QUEST = "UPDATE queste_users SET completed = ? WHERE uuid = ? AND quest = ?";

    protected final Queste plugin;
    protected final ConcurrentMap<UUID, QuesteAccount> cachedAccounts = new ConcurrentHashMap<>();

    protected SQLCommonStorage(Queste plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<QuesteAccount> getAccount(UUID uuid) {
        // Add a check to ensure accounts aren't taking a long time
        long startTime = System.currentTimeMillis();
        CompletableFuture<QuesteAccount> future = new CompletableFuture<>();
        if (cachedAccounts.containsKey(uuid)) {
            future.complete(cachedAccounts.get(uuid));
        } else {
            DB.getResultsAsync(SELECT_QUEST, getDatabaseUuid(uuid)).whenComplete((results, err) -> {
                Map<Quest, Boolean> quests = new HashMap<>();
                for (DbRow row : results) {
                    String questName = row.getString("quest");
                    Quest quest = plugin.getManagers().getQuesteCache().getQuest(questName);
                    if (quest == null) {
                        plugin.getLogger().warning("Cannot find quest " + questName);
                        continue;
                    }

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
                    quests.put(quest, completed);
                }

                QuesteAccount account = new QuesteAccount(uuid);
                quests.forEach((quest, completed) -> {
                    if (completed) account.addCompletedQuest(quest);
                    else account.addActiveQuest(quest);
                });

                cachedAccounts.put(uuid, account);
                Bukkit.getScheduler().runTask(plugin, () -> future.complete(account));

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
                current.put(row.getString("quest"), row.getInt("completed") == 1);
            }

            for (Quest completedQuest : account.getCompletedQuests()) {
                if (!current.containsKey(completedQuest.getName())) {
                    try {
                        DB.executeInsert(INSERT_QUEST, getDatabaseUuid(uuid), completedQuest.getName(), true);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                } else {
                    DB.executeUpdateAsync(UPDATE_QUEST, true, getDatabaseUuid(uuid), completedQuest.getName());
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
                    DB.executeUpdateAsync(UPDATE_QUEST, true, getDatabaseUuid(uuid), activeQuest.getName());
                }
            }

            DB.getResultsAsync(SELECT_OBJECTIVE, getDatabaseUuid(uuid)).thenAccept(objectiveResults -> {
                Map<String, Integer> currentProgress = new HashMap<>();
                for (DbRow row : objectiveResults) {
                    currentProgress.put(row.getString("objective"), row.getInt("progress"));
                }

                account.getAllQuests().forEach(quest -> {
                    quest.getObjectives().forEach(objective -> {
                        Player player = Bukkit.getPlayer(uuid);
                        int progress = objective.getIncrement(player);
                        if (!currentProgress.containsKey(objective.getSafeName())) {
                            try {
                                DB.executeInsert(INSERT_OBJECTIVE, getDatabaseUuid(uuid), objective.getSafeName(), progress);
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                        } else {
                            DB.executeUpdateAsync(UPDATE_OBJECTIVE, progress, getDatabaseUuid(uuid), objective.getSafeName());
                        }
                        objective.untrack(uuid);
                    });
                });
            });

            cachedAccounts.remove(uuid);
        }).exceptionally(t -> {
            t.printStackTrace();
            return null;
        });
    }
}
