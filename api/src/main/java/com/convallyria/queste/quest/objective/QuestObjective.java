package com.convallyria.queste.quest.objective;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.api.QuesteAPI;
import com.convallyria.queste.gui.GuiEditable;
import com.convallyria.queste.gui.IGuiEditable;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.translation.Translations;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public abstract class QuestObjective implements Listener, Keyed, IGuiEditable {

    private transient IQuesteAPI plugin;

    private final String questName;
    @GuiEditable("Completion Amount")
    private int completionAmount;
    private transient Map<UUID, Integer> progress;
    @GuiEditable("Story Mode Key")
    private int storyModeKey;
    @GuiEditable("Display Name")
    private String displayName;
    @GuiEditable("Show Particle Effects")
    private boolean particles;

    protected QuestObjective(IQuesteAPI plugin, Quest quest) {
        this.plugin = plugin;
        this.questName = quest.getName();
        this.completionAmount = 10;
        this.progress = new ConcurrentHashMap<>();
        // EDIT: Auto set as next highest story key.
        // (*Auto set it as first - maybe change this in the future to set it as last compared to other objectives.*)
        this.storyModeKey = quest.findNextStoryKey();
        this.particles = true;
        if (getPluginRequirement() != null
                && Bukkit.getPluginManager().getPlugin(getPluginRequirement()) == null) {
            plugin.getLogger().warning("Objective " + getName() + " requires plugin "
                    + getPluginRequirement()
                    + " which is not loaded. Objective will be skipped for event registration.");
            return;
        }
        Bukkit.getPluginManager().registerEvents(this, (Plugin) plugin);
    }

    /**
     * @deprecated Use discouraged. Only used internally.
     * @see #increment(Player)
     * @see #setIncrement(Player, int)
     * @see #getIncrement(Player)
     * @see #checkIfCanIncrement(Quest, Player)
     * @param progress progress map
     */
    @Deprecated
    public void setProgress(Map<UUID, Integer> progress) {
        this.progress = progress;
    }

    @Override
    @NotNull
    public NamespacedKey getKey() {
        return new NamespacedKey((Plugin) getPlugin(), getQuestName() + "/" + getSafeName());
    }

    @NotNull
    public IQuesteAPI getPlugin() {
        if (plugin == null) this.plugin = QuesteAPI.getAPI();
        return plugin;
    }

    /**
     * Gets the name of the associated quest
     * @return associated quest
     */
    @NotNull
    public String getQuestName() {
        return questName;
    }

    /**
     * Gets the quest this objective is associated with.
     * @return linked quest, possibly null
     */
    @Nullable
    public Quest getQuest() {
        return getPlugin().getManagers().getQuesteCache().getQuest(getQuestName());
    }

    /**
     * Gets how many times this objective must be completed.
     * @return completion amount
     */
    public int getCompletionAmount() {
        return completionAmount;
    }

    /**
     * Sets how many times this objective must be completed.
     * @param completionAmount times to complete
     */
    public void setCompletionAmount(int completionAmount) {
        this.completionAmount = completionAmount;
    }

    /**
     * Gets the current completion amount for the specified player.
     * @param player player to check
     * @return completion amount
     * @see #getCompletionAmount()
     * @see #setCompletionAmount(int)
     * @see #increment(Player)
     * @see #checkIfCanIncrement(Quest, Player)
     * @see #setIncrement(Player, int)
     */
    public int getIncrement(@NotNull Player player) {
        return progress.getOrDefault(player.getUniqueId(), 0);
    }

    /**
     * Increments the current completion amount for the specified player by 1.
     * @param player player to check
     * @return {@link CompletableFuture} with a {@link Boolean} stating whether the increment was successful
     * @see #getCompletionAmount()
     * @see #setCompletionAmount(int)
     * @see #increment(Player)
     * @see #checkIfCanIncrement(Quest, Player)
     * @see #setIncrement(Player, int)
     */
    public CompletableFuture<Boolean> increment(@NotNull Player player) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        getPlugin().getManagers().getStorageManager().getAccount(player.getUniqueId()).thenAccept(account -> {
            Quest quest = getQuest();
            if (quest == null) {
                future.complete(false);
                return;
            }

            if (!account.getActiveQuests().contains(quest) || !checkIfCanIncrement(quest, player)) {
                future.complete(false);
                return;
            }

            // Increase progress, update bossbar, if this objective is completed play effects
            progress.put(player.getUniqueId(), progress.getOrDefault(player.getUniqueId(), 0) + 1);
            future.complete(true);
            if (progress.get(player.getUniqueId()) >= completionAmount) {
                QuestObjective currentObjective = quest.getCurrentObjective(player);
                if (currentObjective != null) {
                    Translations.OBJECTIVE_COMPLETE.sendList(player, this.getStoryModeKey() + 1,
                            quest.getObjectives().size(),
                            this.getStoryModeKey() + 2,
                            quest.getObjectives().size(),
                            currentObjective.getName());
                    player.playSound(player.getLocation(), Sound.UI_TOAST_IN, 1f, 1f);
                    if (plugin.getConfig().getBoolean("settings.server.advancements.generate")) {
                        Advancement advancement = Bukkit.getAdvancement(getKey());
                        AdvancementProgress progress = player.getAdvancementProgress(advancement);
                        progress.awardCriteria("impossible");
                    }
                }
                quest.tryComplete(player); // Attempt completion of quest
            }
            account.update(quest);
        }).exceptionally(err -> {
            err.printStackTrace();
            return null;
        });
        return future;
    }

    /**
     * Checks whether this objective can be incremented by the player.
     * @param quest quest to check compared to this
     * @param player player to check
     * @return true if player can increment, false otherwise
     * @see #getCompletionAmount()
     * @see #setCompletionAmount(int)
     * @see #increment(Player)
     * @see #checkIfCanIncrement(Quest, Player)
     * @see #setIncrement(Player, int)
     */
    public boolean checkIfCanIncrement(@NotNull Quest quest, @NotNull Player player) {
        for (QuestObjective otherObjective : quest.getObjectives()) {
            // If the player has not completed another objective, story mode is enabled, and our story
            // is later on, do not continue.
            if (!otherObjective.hasCompleted(player)
                    && quest.isStoryMode()
                    && this.getStoryModeKey() > otherObjective.getStoryModeKey()) {
                if (plugin.debug()) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            new TextComponent(ChatColor.RED + "You cannot complete the objective " + getName() + " yet."));
                }
                return false;
            }
        }
        return true;
    }

    public void setIncrement(@NotNull Player player, int increment) {
        progress.put(player.getUniqueId(), increment);
    }

    public void untrack(UUID uuid) {
        progress.remove(uuid);
    }

    public int getStoryModeKey() {
        return storyModeKey;
    }

    public void setStoryModeKey(int storyModeKey) {
        this.storyModeKey = storyModeKey;
    }

    /**
     * Returns whether the player has completed this objective.
     * @param player player to check
     * @return true if completed, false otherwise
     */
    public boolean hasCompleted(@NotNull Player player) {
        return progress.getOrDefault(player.getUniqueId(), 0) == completionAmount;
    }

    public String getDisplayName() {
        if (displayName == null) displayName = getName();
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSafeName() {
        return getName().replace(" ", "_");
    }

    /**
     * The plugin that this objective requires.
     * @return required plugin, possibly null
     */
    @Nullable
    public String getPluginRequirement() {
        return null;
    }
}
