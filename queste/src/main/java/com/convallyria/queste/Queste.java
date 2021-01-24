package com.convallyria.queste;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import co.aikar.idb.DB;
import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.api.QuesteAPI;
import com.convallyria.queste.command.QuestCommand;
import com.convallyria.queste.command.QuestObjectiveCommand;
import com.convallyria.queste.command.QuesteCommand;
import com.convallyria.queste.command.QuestsCommand;
import com.convallyria.queste.gson.ConfigurationSerializableAdapter;
import com.convallyria.queste.gson.QuestAdapter;
import com.convallyria.queste.listener.PlayerQuitListener;
import com.convallyria.queste.managers.QuesteManagers;
import com.convallyria.queste.managers.registry.QuesteRegistry;
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
import com.convallyria.queste.quest.objective.QuestObjective;
import com.convallyria.queste.quest.objective.QuestObjectiveRegistry;
import com.convallyria.queste.quest.objective.ReachLocationObjective;
import com.convallyria.queste.quest.objective.ShearSheepQuestObjective;
import com.convallyria.queste.quest.objective.citizens.CitizenInteractQuestObjective;
import com.convallyria.queste.quest.objective.dungeonsxl.FinishDungeonFloorQuestObjective;
import com.convallyria.queste.quest.objective.dungeonsxl.FinishDungeonQuestObjective;
import com.convallyria.queste.quest.objective.dungeonsxl.KillDungeonMobQuestObjective;
import com.convallyria.queste.quest.objective.rpgregions.DiscoverRegionQuestObjective;
import com.convallyria.queste.quest.requirement.ItemRequirement;
import com.convallyria.queste.quest.requirement.LevelRequirement;
import com.convallyria.queste.quest.requirement.MoneyRequirement;
import com.convallyria.queste.quest.requirement.QuestQuestRequirement;
import com.convallyria.queste.quest.requirement.QuestRequirement;
import com.convallyria.queste.quest.requirement.QuestRequirementRegistry;
import com.convallyria.queste.quest.reward.ConsoleCommandReward;
import com.convallyria.queste.quest.reward.ExperienceReward;
import com.convallyria.queste.quest.reward.ItemReward;
import com.convallyria.queste.quest.reward.MessageReward;
import com.convallyria.queste.quest.reward.MoneyReward;
import com.convallyria.queste.quest.reward.PlayerCommandReward;
import com.convallyria.queste.quest.reward.QuestReward;
import com.convallyria.queste.quest.reward.QuestRewardRegistry;
import com.convallyria.queste.translation.Translations;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import hu.trigary.advancementcreator.AdvancementFactory;
import net.islandearth.languagy.api.language.Language;
import net.islandearth.languagy.api.language.LanguagyImplementation;
import net.islandearth.languagy.api.language.LanguagyPluginHook;
import net.islandearth.languagy.api.language.Translator;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;

public final class Queste extends JavaPlugin implements IQuesteAPI, LanguagyPluginHook {

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

    private QuesteManagers managers;
    private AdvancementFactory advancementFactory;

    public AdvancementFactory getAdvancementFactory() {
        return advancementFactory;
    }

    @Override
    public void onEnable() {
        QuesteAPI.setAPI(this);
        this.createConfig();
        this.generateLang();
        this.managers = new QuesteManagers(this);
        this.registerObjectives();
        this.registerRewards();
        this.registerRequirements();
        try {
            this.hook(this);
            this.registerCommands();
            this.registerListeners();
            new Metrics(this, 9954);
            this.advancementFactory = new AdvancementFactory(this, true, false);
        } catch (Exception e) { // MockBukkit support. Throws an exception stating commands are unsupported.
            getLogger().log(Level.WARNING, "Unable to initialise listeners/commands", e);
        }
    }

    @Override
    public void onDisable() {
        if (getManagers() != null) {
            getManagers().getQuesteCache().getQuests().values().forEach(quest -> quest.save(this));
            getManagers().getStorageManager().getCachedAccounts().forEach((uuid, account) -> {
                for (Quest activeQuest : account.getActiveQuests()) {
                    if (activeQuest.getTime() != 0) {
                        account.removeActiveQuest(activeQuest);
                    }
                }
                getManagers().getStorageManager().removeCachedAccount(uuid);
            });
            getManagers().getQuesteCache().getQuests().clear();
        } else {
            getLogger().warning("Unable to save data because managers were null.");
        }
        QuesteAPI.setAPI(null);
        DB.close();
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
        config.addDefault("settings.server.advancements.generate", true);
        config.addDefault("settings.dev.debug", false);
        config.addDefault("settings.storage.mode", "file");
        config.addDefault("settings.sql.host", "localhost");
        config.addDefault("settings.sql.port", 3306);
        config.addDefault("settings.sql.db", "Queste");
        config.addDefault("settings.sql.user", "user");
        config.addDefault("settings.sql.pass", "pass");
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
    }

    private void registerCommandCompletions(PaperCommandManager manager) {
        CommandCompletions<BukkitCommandCompletionContext> commandCompletions = manager.getCommandCompletions();
        // Quests
        commandCompletions.registerAsyncCompletion("quests", context -> managers.getQuesteCache().getQuests().keySet());
        // Objectives, Rewards, Requirements, etc - all registries
        for (QuesteRegistry<?> questeRegistry : managers.getQuestRegistry().values()) {
            commandCompletions.registerAsyncCompletion(questeRegistry.getRegistryName(), context -> questeRegistry.get().keySet());
        }
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
        QuesteRegistry<QuestObjective> registry = (QuestObjectiveRegistry) managers.getQuestRegistry(QuestObjectiveRegistry.class);
        if (registry == null) {
            getLogger().warning("Unable to register objectives");
            return;
        }
        registry.register(ShearSheepQuestObjective.class);
        registry.register(BreakBlockQuestObjective.class);
        registry.register(BreedQuestObjective.class);
        registry.register(BucketFillObjective.class);
        registry.register(DiscoverRegionQuestObjective.class);
        registry.register(EnchantQuestObjective.class);
        registry.register(FishQuestObjective.class);
        registry.register(InteractEntityObjective.class);
        registry.register(KillEntityQuestObjective.class);
        registry.register(LevelQuestObjective.class);
        registry.register(PlaceBlockQuestObjective.class);
        registry.register(CitizenInteractQuestObjective.class);
        registry.register(FinishDungeonFloorQuestObjective.class);
        registry.register(FinishDungeonQuestObjective.class);
        registry.register(KillDungeonMobQuestObjective.class);
        registry.register(ReachLocationObjective.class);
    }

    private void registerRewards() {
        QuesteRegistry<QuestReward> registry = (QuestRewardRegistry) managers.getQuestRegistry(QuestRewardRegistry.class);
        if (registry == null) {
            getLogger().warning("Unable to register rewards");
            return;
        }
        registry.register(ConsoleCommandReward.class);
        registry.register(ExperienceReward.class);
        registry.register(ItemReward.class);
        registry.register(MessageReward.class);
        registry.register(MoneyReward.class);
        registry.register(PlayerCommandReward.class);
    }

    private void registerRequirements() {
        QuesteRegistry<QuestRequirement> registry = (QuestRequirementRegistry) managers.getQuestRegistry(QuestRequirementRegistry.class);
        if (registry == null) {
            getLogger().warning("Unable to register requirements");
            return;
        }
        registry.register(LevelRequirement.class);
        registry.register(ItemRequirement.class);
        registry.register(MoneyRequirement.class);
        registry.register(QuestQuestRequirement.class);
    }

    @Override
    public Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Quest.class, new QuestAdapter())
                .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ConfigurationSerializableAdapter())
                .setPrettyPrinting()
                .serializeNulls()
                .create();
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
