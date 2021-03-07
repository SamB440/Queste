package com.convallyria.queste.config;

import com.convallyria.queste.Queste;
import com.convallyria.queste.api.QuesteAPI;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public enum Configurations {
    USE_ROMAN_NUMERALS("settings.server.use_roman_numerals", false),
    GENERATE_ADVANCEMENTS("settings.server.advancements.generate", true),
    JOURNAL_ENABLED("settings.server.player.journal.enabled", true),
    JOURNAL_SLOT("settings.server.player.journal.slot", 8),
    JOURNAL_REMOVABLE("settings.server.player.journal.removable", false),
    DEBUG("settings.dev.debug", false),
    
    
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
    }
}
