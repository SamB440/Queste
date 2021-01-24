package com.convallyria.queste.managers.data.sqlite;

import co.aikar.idb.DB;
import co.aikar.idb.Database;
import co.aikar.idb.DatabaseOptions;
import co.aikar.idb.PooledDatabaseOptions;
import com.convallyria.queste.Queste;
import com.convallyria.queste.managers.data.SQLCommonStorage;

import java.sql.SQLException;

public class SqliteStorage extends SQLCommonStorage {

    public SqliteStorage(Queste plugin) {
        super(plugin);
        DatabaseOptions options = DatabaseOptions.builder().sqlite(plugin.getDataFolder() + "/users.sqlite").build();
        Database db = PooledDatabaseOptions.builder().options(options).createHikariDatabase();
        DB.setGlobalDatabase(db);
        try {
            db.executeUpdate(CREATE_TABLE);
            db.executeUpdate(CREATE_OBJECTIVE_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
