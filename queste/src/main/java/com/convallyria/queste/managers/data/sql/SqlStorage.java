package com.convallyria.queste.managers.data.sql;

import co.aikar.idb.DB;
import co.aikar.idb.Database;
import co.aikar.idb.DatabaseOptions;
import co.aikar.idb.PooledDatabaseOptions;
import com.convallyria.queste.Queste;
import com.convallyria.queste.config.Configurations;
import com.convallyria.queste.managers.data.SQLCommonStorage;

import java.sql.SQLException;

public class SqlStorage extends SQLCommonStorage {

    public SqlStorage(Queste plugin) {
        super(plugin);
        DatabaseOptions options = DatabaseOptions.builder().mysql(plugin.getConfig().getString("settings.sql.user"),
                Configurations.SQL_PASS.getString(),
                Configurations.SQL_DB.getString(),
                Configurations.SQL_HOST.getString() + ":" + Configurations.SQL_PORT.getInt()).build();
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
