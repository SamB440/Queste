package com.convallyria.queste.quest.objective;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.translation.Translations;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public abstract class QuestObjective implements Listener {

    private transient Queste plugin;

    private final String questName;
    private final QuestObjectiveEnum type;
    private int completionAmount;
    private final Map<UUID, Integer> progress;
    private int storyModeKey;

    public QuestObjective(Queste plugin, @NotNull QuestObjectiveEnum type, Quest quest) {
        this.plugin = plugin;
        this.questName = quest.getName();
        this.type = type;
        this.completionAmount = 10;
        this.progress = new ConcurrentHashMap<>();
        this.storyModeKey = 0; // Auto set it as first - maybe change this in the future to set it as last compared to other objectives.
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @NotNull
    public Queste getPlugin() {
        if (plugin == null) this.plugin = JavaPlugin.getPlugin(Queste.class);
        return plugin;
    }

    @NotNull
    public String getQuestName() {
        return questName;
    }

    @Nullable
    public Quest getQuest() {
        return getPlugin().getManagers().getQuesteCache().getQuest(getQuestName());
    }

    @NotNull
    public QuestObjectiveEnum getType() {
        return type;
    }

    public int getCompletionAmount() {
        return completionAmount;
    }

    public void setCompletionAmount(int completionAmount) {
        this.completionAmount = completionAmount;
    }

    public int getIncrement(@NotNull Player player) {
        return progress.getOrDefault(player.getUniqueId(), 0);
    }

    public CompletableFuture<Boolean> increment(@NotNull Player player) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        getPlugin().getManagers().getStorageManager().getAccount(player.getUniqueId()).thenAccept(account -> {
            account.getActiveQuests().forEach(quest -> {
                if (quest.getName().equals(this.getQuestName())) {
                    for (QuestObjective otherObjective : quest.getObjectives()) {
                        // If the player has not completed another objective, story mode is enabled, and our story
                        // is later on, do not continue.
                        if (!otherObjective.hasCompleted(player)
                                && quest.isStoryMode()
                                && this.getStoryModeKey() > otherObjective.getStoryModeKey()) {
                            if (plugin.debug()) {
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "You cannot complete the objective " + type.getName() + " yet."));
                            }
                            future.complete(false);
                            return;
                        }
                    }

                    // Increase progress, update bossbar, if this objective is completed play effects
                    progress.put(player.getUniqueId(), progress.getOrDefault(player.getUniqueId(), 0) + 1);
                    account.update(quest);
                    future.complete(true);
                    if (progress.get(player.getUniqueId()) >= completionAmount) {
                        QuestObjective currentObjective = quest.getCurrentObjective(player);
                        if (currentObjective != null) {
                            Translations.OBJECTIVE_COMPLETE.sendList(player, this.getStoryModeKey() + 1,
                                    quest.getObjectives().size(),
                                    this.getStoryModeKey() + 2,
                                    quest.getObjectives().size(),
                                    currentObjective.getType().getName());
                            player.playSound(player.getLocation(), Sound.UI_TOAST_IN, 1f, 1f);
                        }
                        quest.tryComplete(player); // Attempt completion of quest
                    }
                }
            });
        }).exceptionally(err -> {
            err.printStackTrace();
            return null;
        });
        return future;
    }

    public void setIncrement(@NotNull Player player, int increment) {
        progress.put(player.getUniqueId(), increment);
    }

    public int getStoryModeKey() {
        return storyModeKey;
    }

    public void setStoryModeKey(int storyModeKey) {
        this.storyModeKey = storyModeKey;
    }

    public boolean hasCompleted(@NotNull Player player) {
        return progress.getOrDefault(player.getUniqueId(), 0) == completionAmount;
    }

    public enum QuestObjectiveEnum {
        PLACE_BLOCK(PlaceBlockQuestObjective.class, "Place Block"),
        BREAK_BLOCK(BreakBlockQuestObjective.class, "Break Block"),
        DISCOVER_REGION(DiscoverRegionQuestObjective.class, "Discover Region", "RPGRegions"),
        BREED(BreedQuestObjective.class, "Breed Animals"),
        SHEAR_SHEEP(ShearSheepQuestObjective.class, "Shear Sheep"),
        FISH(FishQuestObjective.class, "Fish"),
        ENCHANT(EnchantQuestObjective.class, "Enchant Item"),
        KILL_ENTITY(KillEntityQuestObjective.class, "Kill Entity"),
        LEVEL(LevelQuestObjective.class, "Level Up"),
        FILL_BUCKET(BucketFillObjective.class, "Fill Bucket"),
        INTERACT_ENTITY(InteractEntityObjective.class, "Interact With Entity");

        private final Class<? extends QuestObjective> clazz;
        private final String name;
        private final String pluginName;

        QuestObjectiveEnum(Class<? extends QuestObjective> clazz, String name) {
            this.clazz = clazz;
            this.name = name;
            this.pluginName = null;
        }

        QuestObjectiveEnum(Class<? extends QuestObjective> clazz, String name, String pluginName) {
            this.clazz = clazz;
            this.name = name;
            this.pluginName = pluginName;
        }

        @Nullable
        public QuestObjective getNewObjective(Queste plugin, Quest quest) {
            if (getPluginRequirement() != null) {
                if (Bukkit.getPluginManager().getPlugin(getPluginRequirement()) == null) {
                    return null;
                }
            }

            try {
                Constructor<?> constructor = clazz.getConstructor(Queste.class, Quest.class);
                return (QuestObjective) constructor.newInstance(plugin, quest);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
            return null;
        }

        public String getName() {
            return name;
        }

        public String getPluginRequirement() {
            return pluginName;
        }
    }
}
