package com.convallyria.queste;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import com.convallyria.queste.api.QuesteAPI;
import com.convallyria.queste.command.QuestCommand;
import com.convallyria.queste.command.QuestObjectiveCommand;
import com.convallyria.queste.command.QuesteCommand;
import com.convallyria.queste.command.QuestsCommand;
import com.convallyria.queste.gson.LocationAdapter;
import com.convallyria.queste.gson.QuestAdapter;
import com.convallyria.queste.listener.PlayerQuitListener;
import com.convallyria.queste.managers.QuesteManagers;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.quest.objective.BreakBlockQuestObjective;
import com.convallyria.queste.quest.objective.BreedQuestObjective;
import com.convallyria.queste.quest.objective.BucketFillObjective;
import com.convallyria.queste.quest.objective.EnchantQuestObjective;
import com.convallyria.queste.quest.objective.FishQuestObjective;
import com.convallyria.queste.quest.objective.InteractEntityObjective;
import com.convallyria.queste.quest.objective.KillEntityQuestObjective;
import com.convallyria.queste.quest.objective.LevelQuestObjective;
import com.convallyria.queste.quest.objective.PlaceBlockQuestObjective;
import com.convallyria.queste.quest.objective.QuestObjectiveRegistry;
import com.convallyria.queste.quest.objective.ShearSheepQuestObjective;
import com.convallyria.queste.quest.objective.citizens.CitizenInteractQuestObjective;
import com.convallyria.queste.quest.objective.dungeonsxl.FinishDungeonFloorQuestObjective;
import com.convallyria.queste.quest.objective.dungeonsxl.FinishDungeonQuestObjective;
import com.convallyria.queste.quest.objective.dungeonsxl.KillDungeonMobQuestObjective;
import com.convallyria.queste.quest.objective.rpgregions.DiscoverRegionQuestObjective;
import com.convallyria.queste.quest.reward.QuestReward;
import com.convallyria.queste.translation.Translations;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.islandearth.languagy.api.language.Language;
import net.islandearth.languagy.api.language.LanguagyImplementation;
import net.islandearth.languagy.api.language.LanguagyPluginHook;
import net.islandearth.languagy.api.language.Translator;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public final class Queste extends JavaPlugin implements QuesteAPI, LanguagyPluginHook {

    // This is for normal instantiation by the server.
    public Queste() {
        super();
    }
    
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
        this.registerObjectives();
        try {
            this.hook(this);
            this.registerCommands();
            this.registerListeners();
            new Metrics(this, 9954);
        } catch (Exception e) { // MockBukkit support. Throws an exception stating commands are unsupported.
            plugin.getLogger().log(Level.WARNING, "Unable to initialise listeners/commands", e);
        }
    }

    @Override
    public void onDisable() {
        getManagers().getQuesteCache().getQuests().values().forEach(quest -> quest.save(this));
        getManagers().getStorageManager().getCachedAccounts().forEach((uuid, account) -> {
            getManagers().getStorageManager().removeCachedAccount(uuid);
        });
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
        registerCommandContexts(manager);
        registerCommandCompletions(manager);
        manager.enableUnstableAPI("help");
        manager.registerCommand(new QuesteCommand(this));
        manager.registerCommand(new QuestCommand(this));
        manager.registerCommand(new QuestObjectiveCommand(this));
        manager.registerCommand(new QuestsCommand(this));
    }

    private void registerCommandContexts(PaperCommandManager manager) {
        // Quest.class
        manager.getCommandContexts().registerContext(Quest.class, context -> {
            String name = context.popFirstArg();
            Quest quest = managers.getQuesteCache().getQuest(name);
            if (quest != null) return quest;
            throw new InvalidCommandArgument("Could not find a quest with that name.");
        });
        // QuestReward.class
        manager.getCommandContexts().registerContext(QuestReward.class, context -> {
            String name = context.popFirstArg();
            QuestReward reward = QuestReward.QuestRewardEnum.valueOf(name).getReward();
            if (reward != null) {
                return reward;
            }
            throw new InvalidCommandArgument("Could not find a reward with that name.");
        });
    }

    private void registerCommandCompletions(PaperCommandManager manager) {
        CommandCompletions<BukkitCommandCompletionContext> commandCompletions = manager.getCommandCompletions();
        // Quests
        commandCompletions.registerAsyncCompletion("quests", context -> managers.getQuesteCache().getQuests().keySet());
        // Objectives
        commandCompletions.registerAsyncCompletion("objectives", context -> managers.getObjectiveRegistry().getObjectives().keySet());
        // Rewards
        commandCompletions.registerAsyncCompletion("rewards", context -> {
            List<String> rewards = new ArrayList<>();
            for (QuestReward.QuestRewardEnum reward : QuestReward.QuestRewardEnum.values()) {
                rewards.add(reward.toString());
            }
            return rewards;
        });
        // Options
        manager.getCommandCompletions().registerAsyncCompletion("options", c -> ImmutableList.of("--force"));
        // Locations
        manager.getCommandCompletions().registerAsyncCompletion("locations", c -> ImmutableList.of("TARGET", "SELF"));
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerQuitListener(this), this);
    }

    private void registerObjectives() {
        QuestObjectiveRegistry registry = managers.getObjectiveRegistry();
        registry.registerObjective(ShearSheepQuestObjective.class);
        registry.registerObjective(BreakBlockQuestObjective.class);
        registry.registerObjective(BreedQuestObjective.class);
        registry.registerObjective(BucketFillObjective.class);
        registry.registerObjective(DiscoverRegionQuestObjective.class);
        registry.registerObjective(EnchantQuestObjective.class);
        registry.registerObjective(FishQuestObjective.class);
        registry.registerObjective(InteractEntityObjective.class);
        registry.registerObjective(KillEntityQuestObjective.class);
        registry.registerObjective(LevelQuestObjective.class);
        registry.registerObjective(PlaceBlockQuestObjective.class);
        registry.registerObjective(CitizenInteractQuestObjective.class);
        registry.registerObjective(FinishDungeonFloorQuestObjective.class);
        registry.registerObjective(FinishDungeonQuestObjective.class);
        registry.registerObjective(KillDungeonMobQuestObjective.class);
    }

    public Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Quest.class, new QuestAdapter())
                .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
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
