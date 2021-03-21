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
import com.convallyria.queste.config.Configurations;
import com.convallyria.queste.gson.ConfigurationSerializableAdapter;
import com.convallyria.queste.gson.QuestAdapter;
import com.convallyria.queste.listener.JournalListener;
import com.convallyria.queste.listener.PlayerConnectionListener;
import com.convallyria.queste.managers.QuesteManagers;
import com.convallyria.queste.managers.data.IStorageManager;
import com.convallyria.queste.managers.data.account.QuesteAccount;
import com.convallyria.queste.managers.registry.QuesteRegistry;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.quest.objective.BreakBlockQuestObjective;
import com.convallyria.queste.quest.objective.BreedQuestObjective;
import com.convallyria.queste.quest.objective.BucketFillObjective;
import com.convallyria.queste.quest.objective.EnchantQuestObjective;
import com.convallyria.queste.quest.objective.FishQuestObjective;
import com.convallyria.queste.quest.objective.HitTargetLocationObjective;
import com.convallyria.queste.quest.objective.InteractEntityObjective;
import com.convallyria.queste.quest.objective.JumpQuestObjective;
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
import com.convallyria.queste.quest.requirement.PermissionRequirement;
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
import com.convallyria.queste.quest.start.NPCQuestStart;
import com.convallyria.queste.quest.start.QuestStart;
import com.convallyria.queste.quest.start.QuestStartRegistry;
import com.convallyria.queste.task.ExpiringQuestTask;
import com.convallyria.queste.task.UpdateBossbarTask;
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
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.util.concurrent.ExecutionException;
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

    private boolean isShuttingDown;

    @Override
    public void onEnable() {
        this.isShuttingDown = false;
        QuesteAPI.setAPI(this);
        this.createConfig();
        this.generateLang();
        this.managers = new QuesteManagers(this);
        this.registerObjectives();
        this.registerRewards();
        this.registerRequirements();
        this.registerStarters();
        try {
            this.hook(this);
            this.registerCommands();
            this.registerListeners();
            new Metrics(this, 9954);
            this.advancementFactory = new AdvancementFactory(this, true, false);
            this.startTasks();
        } catch (Exception e) { // MockBukkit support. Throws an exception stating commands are unsupported.
            getLogger().log(Level.WARNING, "Unable to initialise listeners/commands", e);
        }
    }

    @Override
    public void onDisable() {
        this.isShuttingDown = true;
        IStorageManager storageManager = getManagers().getStorageManager();
        if (getManagers() != null) {
            getManagers().getQuesteCache().getQuests().values().forEach(quest -> quest.save(this));
            for (QuesteAccount account : storageManager.getCachedAccounts().values()) {
                for (Quest activeQuest : account.getActiveQuests()) {
                    if (activeQuest.getTime() != 0) {
                        account.removeActiveQuest(activeQuest);
                    }
                }
                try {
                    storageManager.removeCachedAccount(account.getUuid()).get();
                } catch (InterruptedException | ExecutionException e) {
                    Thread.currentThread().interrupt();
                }
            }

            getManagers().getQuesteCache().getQuests().clear();
        } else {
            getLogger().warning("Unable to save data because managers were null.");
        }
        QuesteAPI.setAPI(null);
        DB.close();
    }

    public boolean isShuttingDown() {
        return isShuttingDown;
    }

    private void createConfig() {
        Configurations.generate(this);
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
            commandCompletions.registerAsyncCompletion(questeRegistry.getRegistryName() + "-presets", context -> questeRegistry.loadAllPresets());
        }
        // Options
        manager.getCommandCompletions().registerAsyncCompletion("options", c -> ImmutableList.of("--force"));
        // Locations
        manager.getCommandCompletions().registerAsyncCompletion("locations", c -> ImmutableList.of("TARGET", "SELF"));
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerConnectionListener(this), this);
        if (Configurations.JOURNAL_ENABLED.getBoolean()) pm.registerEvents(new JournalListener(this), this);
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
        registry.register(JumpQuestObjective.class);
        registry.register(HitTargetLocationObjective.class);
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
        registry.register(PermissionRequirement.class);
    }

    private void registerStarters() {
        QuesteRegistry<QuestStart> registry = (QuestStartRegistry) managers.getQuestRegistry(QuestStartRegistry.class);
        if (registry == null) {
            getLogger().warning("Unable to register starters");
            return;
        }
        registry.register(NPCQuestStart.class);
    }

    private void startTasks() {
        int interval = Configurations.BOSSBAR_INTERVAL.getInt();
        boolean async = Configurations.BOSSBAR_ASYNC.getBoolean();
        if (async) {
            Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new UpdateBossbarTask(this), 20L, interval);
        } else {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new UpdateBossbarTask(this), 20L, interval);
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new ExpiringQuestTask(this), 20L, 20L);
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
        return Configurations.DEBUG.getBoolean();
    }

    public void debug(String debug) {
        this.debug(debug, Level.INFO);
    }

    public void debug(String debug, Level level) {
        if (debug()) this.getLogger().log(level, "[Debug] " + debug);
    }

    @Override
    public void onLanguagyHook() {
        translator.setDisplay(Material.TOTEM_OF_UNDYING);
    }
}
