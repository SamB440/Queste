package com.convallyria.queste.config;

import com.convallyria.queste.Queste;
import com.convallyria.queste.api.QuesteAPI;

import java.util.List;

public enum Configurations {
    USE_ROMAN_NUMERALS("settings.server.use_roman_numerals", false),
    GENERATE_ADVANCEMENTS("settings.server.advancements.generate", true),
    JOURNAL_ENABLED("settings.server.player.journal.enabled", true),
    JOURNAL_SLOT("settings.server.player.journal.slot", 8),
    JOURNAL_REMOVABLE("settings.server.player.journal.removable", false),
    BOSSBAR_ASYNC("settings.server.tasks.bossbar.async", false),
    BOSSBAR_INTERVAL("settings.server.tasks.bossbar.interval", 1),
    USE_ENTITY_TOTEM_EFFECT("settings.server.quest.completed.use_entity_totem_effect", true),
    DEBUG("settings.dev.debug", false),
    STORAGE_MODE("settings.storage.mode", "file"),
    SQL_HOST("settings.sql.host", "localhost"),
    SQL_PORT("settings.sql.port", 3306),
    SQL_DB("settings.sql.host", "Queste"),
    SQL_USER("settings.sql.user", "user"),
    SQL_PASS("settings.sql.host", "pass");
    
    private final String path;
    private final Object def;
    
    Configurations(String path, Object def) {
        this.path = path;
        this.def = def;
    }
    
    public String getPath() {
        return path;
    }
    
    public Object getDefaultValue() {
        return def;
    }
    
    public boolean getBoolean() {
        return QuesteAPI.getAPI().getConfig().getBoolean(path, (Boolean) def);
    }
    
    public String getString() {
        return QuesteAPI.getAPI().getConfig().getString(path, (String) def);
    }
    
    public List<String> getStringList() {
        return QuesteAPI.getAPI().getConfig().getStringList(path);
    }

    public int getInt() {
        return QuesteAPI.getAPI().getConfig().getInt(path, (Integer) def);
    }

    public Object get() {
        return QuesteAPI.getAPI().getConfig().get(path, def);
    }
    
    public static void generate(Queste plugin) {
        plugin.saveDefaultConfig();
    }
}
