package com.convallyria.queste;

import co.aikar.commands.PaperCommandManager;
import com.convallyria.queste.api.QuesteAPI;
import com.convallyria.queste.command.QuestCommand;
import com.convallyria.queste.command.QuesteCommand;
import com.convallyria.queste.managers.QuesteManagers;
import com.convallyria.queste.translation.Translations;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.islandearth.languagy.api.language.Language;
import net.islandearth.languagy.api.language.LanguagyImplementation;
import net.islandearth.languagy.api.language.LanguagyPluginHook;
import net.islandearth.languagy.api.language.Translator;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.util.logging.Level;

public final class Queste extends JavaPlugin implements QuesteAPI, LanguagyPluginHook {

    // This is for MockBukkit.
    protected Queste(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @LanguagyImplementation(Language.ENGLISH)
    private Translator translator;

    public Translator getTranslator() {
        return translator;
    }

    private static Queste plugin;
    private QuesteManagers managers;

    @Override
    public void onEnable() {
        plugin = this;
        this.createConfig();
        this.generateLang();
        this.managers = new QuesteManagers(this);
        try {
            this.registerCommands();
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Unable to initialise commands", e);
        }
    }

    @Override
    public void onDisable() {
        getManagers().getQuesteCache().getQuests().values().forEach(quest -> quest.save(this));
    }

    private void createConfig() {
        FileConfiguration config = this.getConfig();
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
        config.addDefault("settings.dev.debug", false);
        config.addDefault("settings.storage.mode", "file");
        config.options().copyDefaults(true);
        saveConfig();
    }

    private void generateLang() {
        Translations.generateLang(this);
    }

    private void registerCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);
        manager.enableUnstableAPI("help");
        manager.registerCommand(new QuesteCommand(this));
        manager.registerCommand(new QuestCommand(this));
    }

    public Gson getGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }

    public static QuesteAPI getAPI() {
        return plugin;
    }

    @Override
    public QuesteManagers getManagers() {
        return managers;
    }

    @Override
    public boolean debug() {
        return getConfig().getBoolean("settings.dev.debug");
    }

    @Override
    public void onLanguagyHook() {
        translator.setDisplay(Material.TOTEM_OF_UNDYING);
    }
}
