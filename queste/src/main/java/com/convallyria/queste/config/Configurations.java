package com.convallyria.queste.config;

import com.convallyria.queste.Queste;
import com.convallyria.queste.api.QuesteAPI;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.List;

public enum Configurations {
    USE_ROMAN_NUMERALS("settings.server.use_roman_numerals", false),
    GENERATE_ADVANCEMENTS("settings.server.advancements.generate", true),
    JOURNAL_ENABLED("settings.server.player.journal.enabled", true),
    JOURNAL_SLOT("settings.server.player.journal.slot", 8),
    JOURNAL_REMOVABLE("settings.server.player.journal.removable", false),
    BOSSBAR_ASYNC("settings.server.tasks.bossbar.async", false),
    BOSSBAR_INTERVAL("settings.server.tasks.bossbar.interval", 1),
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
        FileConfiguration config = plugin.getConfig();
        String header;
        String eol = System.getProperty("line.separator");
        header = "This is the config for Queste." + eol;
        header += "------ Useful information ------" + eol;
        header += "Documentation can be found at https://fortitude.islandearth.net" + eol;
        header += "Sounds can be found at https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html" + eol;
        header += "------ Support ------" + eol;
        header += "Found a bug? Create an issue at https://gitlab.com/convallyria/queste/issues" + eol;
        header += "Need help? Join our discord at https://discord.gg/fh62mxU" + eol;
        config.options().header(header);
        for (Configurations value : values()) {
            config.addDefault(value.getPath(), value.getDefaultValue());
        }
        config.addDefault("settings.server.gui.general.rows", 6);
        config.addDefault("settings.server.gui.back.posX", 0);
        config.addDefault("settings.server.gui.back.posY", 5);
        config.addDefault("settings.server.gui.back.length", 1);
        config.addDefault("settings.server.gui.back.height", 1);
        config.addDefault("settings.server.gui.forward.posX", 8);
        config.addDefault("settings.server.gui.forward.posY", 5);
        config.addDefault("settings.server.gui.forward.length", 1);
        config.addDefault("settings.server.gui.forward.height", 1);
        config.addDefault("settings.server.gui.exit.posX", 4);
        config.addDefault("settings.server.gui.exit.posY", 5);
        config.addDefault("settings.server.gui.exit.length", 1);
        config.addDefault("settings.server.gui.exit.height", 1);
        config.addDefault("settings.server.gui.exit.command", "");
        config.addDefault("settings.server.gui.exit.show", true);
        config.addDefault("settings.server.gui.pane.posX", 1);
        config.addDefault("settings.server.gui.pane.posY", 1);
        config.addDefault("settings.server.gui.pane.length", 7);
        config.addDefault("settings.server.gui.pane.height", 4);
        config.addDefault("settings.server.gui.outlinePane.posX", 0);
        config.addDefault("settings.server.gui.outlinePane.posY", 0);
        config.addDefault("settings.server.gui.outlinePane.length", 9);
        config.addDefault("settings.server.gui.outlinePane.height", 6);
        config.addDefault("settings.server.gui.outlinePane.show", true);
        config.addDefault("settings.server.gui.outlinePane.mask", Arrays.asList(
                "111111111",
                "100000001",
                "100000001",
                "100000001",
                "100000001",
                "111111111"));
        config.addDefault("settings.server.gui.innerPane.posX", 1);
        config.addDefault("settings.server.gui.innerPane.posY", 1);
        config.addDefault("settings.server.gui.innerPane.length", 7);
        config.addDefault("settings.server.gui.innerPane.height", 4);
        config.addDefault("settings.server.gui.innerPane.show", true);
        config.addDefault("settings.server.gui.innerPane.mask", Arrays.asList(
                "1111111",
                "1111111",
                "1111111",
                "1111111"));
        config.addDefault("settings.server.gui.forward.forward", Material.ARROW.name());
        config.addDefault("settings.server.gui.back.back", Material.ARROW.name());
        config.addDefault("settings.server.gui.exit.exit", Material.BARRIER.name());
        config.addDefault("settings.server.gui.outlinePane.outlinePane", Material.GRAY_STAINED_GLASS_PANE.name());
        config.addDefault("settings.server.gui.innerPane.innerPane", Material.WHITE_STAINED_GLASS_PANE.name());
        config.options().copyDefaults(true);
        plugin.saveConfig();
    }
}
