package com.convallyria.queste.managers.data.sql;

import co.aikar.idb.DB;
import co.aikar.idb.Database;
import co.aikar.idb.DatabaseOptions;
import co.aikar.idb.PooledDatabaseOptions;
import com.convallyria.queste.managers.data.account.QuesteAccount;
import com.convallyria.queste.Queste;
import com.convallyria.queste.managers.data.StorageManager;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SqlStorage implements StorageManager {

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS queste_quests (uuid varchar(32) NOT NULL, region varchar(32) NOT NULL, time varchar(64) NOT NULL, PRIMARY KEY(uuid, region))";

    private ConcurrentMap<UUID, QuesteAccount> cachedAccounts = new ConcurrentHashMap<>();

    private final Queste plugin;

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
    }

    @Override
    public void removeCachedAccount(UUID uuid) {
        QuesteAccount account = cachedAccounts.get(uuid);

    }
}
