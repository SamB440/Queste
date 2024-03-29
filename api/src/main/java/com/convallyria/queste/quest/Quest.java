package com.convallyria.queste.quest;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.api.QuesteAPI;
import com.convallyria.queste.api.event.QuestCompleteEvent;
import com.convallyria.queste.api.event.QuestStartEvent;
import com.convallyria.queste.gui.IGuiEditable;
import com.convallyria.queste.managers.registry.RegistryAccepting;
import com.convallyria.queste.quest.objective.QuestObjective;
import com.convallyria.queste.quest.requirement.QuestRequirement;
import com.convallyria.queste.quest.reward.QuestReward;
import com.convallyria.queste.quest.start.QuestStart;
import com.convallyria.queste.translation.Translations;
import com.convallyria.queste.util.TimeUtils;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class Quest implements Keyed, RegistryAccepting {

    private transient IQuesteAPI plugin;
    private final String name;
    private String displayName;
    private boolean canRestart;
    private final List<QuestObjective> objectives;
    private final List<QuestReward> rewards;
    private final List<QuestRequirement> requirements;
    private final List<QuestStart> starters;
    private boolean storyMode;
    private Sound completeSound;
    private int time;
    private Material icon;
    private String description;
    private boolean dummy;
    private int cooldown;

    public Quest(@NotNull String name) {
        this.name = name;
        this.objectives = new ArrayList<>();
        this.rewards = new ArrayList<>();
        this.requirements = new ArrayList<>();
        this.starters = new ArrayList<>();
        this.completeSound = Sound.UI_TOAST_CHALLENGE_COMPLETE;
        this.icon = Material.TOTEM_OF_UNDYING;
        this.description = "A quest of great honour!";
    }

    public Quest(@NotNull String name, @NotNull Quest quest) {
        this.name = name;
        this.displayName = quest.getDisplayName();
        this.canRestart = quest.canRestart();
        this.objectives = quest.getObjectives();
        this.rewards = quest.getRewards();
        this.requirements = quest.getRequirements();
        this.starters = quest.getStarters();
        this.storyMode = quest.isStoryMode();
        this.completeSound = quest.getCompleteSound();
        this.time = quest.getTime();
        this.icon = quest.getIcon();
        this.description = quest.getDescription();
        this.dummy = quest.isDummy();
    }

    @Override
    @NotNull
    public NamespacedKey getKey() {
        return new NamespacedKey((Plugin) QuesteAPI.getAPI(), getSafeName() + "/root");
    }

    /**
     * Gets the unique name of this quest.
     * @return unique quest name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets a name safe to use everywhere, an example being the {@link NamespacedKey}.
     * @return safe name of this quest
     */
    public String getSafeName() {
        return getName().replace(" ", "_");
    }

    /**
     * Gets the display name for this quest.
     * @return quest display name
     */
    public String getDisplayName() {
        if (displayName == null) this.displayName = name;
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean canRestart() {
        return canRestart;
    }

    public void setCanRestart(boolean canRestart) {
        this.canRestart = canRestart;
    }

    public boolean addObjective(@NotNull QuestObjective objective) {
        if (dummy
            && !getObjectivesFromType(objective.getClass()).isEmpty()) {
            return false;
        }
        objectives.add(objective);
        return true;
    }

    public void removeObjective(QuestObjective objective) {
        objectives.remove(objective);
    }

    public List<QuestObjective> getObjectives() {
        return objectives;
    }

    /**
     * Gets a list of {@link QuestObjective}s by type.
     * @param type type of objective to check
     * @return list of {@link QuestObjective}s
     */
    @NotNull
    public List<QuestObjective> getObjectivesFromType(Class<? extends QuestObjective> type) {
        List<QuestObjective> objectivesByType = new ArrayList<>();
        for (QuestObjective objective : getObjectives()) {
            if (objective.getClass().isInstance(type)) {
                objectivesByType.add(objective);
            }
        }
        return objectivesByType;
    }

    public int findNextStoryKey() {
        if (!storyMode || objectives.isEmpty()) return 0;
        List<QuestObjective> sortedObjectives = Lists.reverse(objectives.stream()
                .sorted(Comparator.comparingInt(QuestObjective::getStoryModeKey))
                .collect(Collectors.toList()));
        return sortedObjectives.get(0).getStoryModeKey() + 1;
    }

    public void addReward(@NotNull QuestReward reward) {
        rewards.add(reward);
    }

    public void removeReward(@NotNull QuestReward reward) {
        rewards.remove(reward);
    }

    public List<QuestReward> getRewards() {
        return rewards;
    }

    public void addRequirement(@NotNull QuestRequirement requirement) {
        requirements.add(requirement);
    }

    public void removeRequirement(@NotNull QuestRequirement requirement) {
        requirements.remove(requirement);
    }

    public List<QuestRequirement> getRequirements() {
        return requirements;
    }

    public void addStarter(@NotNull QuestStart starter) {
        starters.add(starter);
    }

    public void removeStarter(@NotNull QuestStart starter) {
        starters.remove(starter);
    }

    public List<QuestStart> getStarters() {
        return starters;
    }

    public boolean isStoryMode() {
        return storyMode;
    }

    public void setStoryMode(boolean storyMode) {
        this.storyMode = storyMode;
    }

    public Sound getCompleteSound() {
        return completeSound;
    }

    public void setCompleteSound(Sound completeSound) {
        this.completeSound = completeSound;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Material getIcon() {
        return icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDummy() {
        return dummy;
    }

    /**
     * Sets whether a quest is a dummy or not.
     * A dummy quest is one that is only used for configuration reasons.
     * Objectives of the same type cannot be added.
     * @param dummy whether the quest should be a dummy quest
     */
    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    /**
     * Gets the current objective a player should be on.
     * @param player player to check
     * @return current {@link QuestObjective}
     */
    @Nullable
    public QuestObjective getCurrentObjective(@NotNull Player player) {
        if (isStoryMode()) {
            QuestObjective currentObjective = null;
            for (QuestObjective objective : getObjectives()) {
                if (currentObjective == null && !objective.hasCompleted(player)) {
                    currentObjective = objective;
                    continue;
                }

                if (currentObjective != null
                        && objective.getStoryModeKey() < currentObjective.getStoryModeKey()
                        && !objective.hasCompleted(player)) {
                    currentObjective = objective;
                }
            }
            return currentObjective;
        }
        return null;
    }

    /**
     * @see #isCompleted(UUID)
     */
    public boolean isCompleted(@NotNull Player player) {
        return isCompleted(player.getUniqueId());
    }
    /**
     * Checks if a player has completed this quest.
     * @param uuid player to check
     * @return true if all quest objectives are completed, false otherwise
     */
    public boolean isCompleted(@NotNull UUID uuid) {
        boolean objectivesCompleted = true;
        for (QuestObjective objective : objectives) {
            if (!objective.hasCompleted(uuid)) {
                objectivesCompleted = false;
                break;
            }
        }
        return objectivesCompleted;
    }

    /**
     * Attempts to complete a quest for a player.
     * @param player player to complete quest for
     * @return true if objectives are completed and player has completed quest entirely, false otherwise
     */
    public boolean tryComplete(@NotNull Player player) {
        if (isCompleted(player)) {
            forceComplete(player);
            return true;
        }
        return false;
    }

    public void forceComplete(Player player) {
        giveEffectsAndRewards(player);
        getPlugin().getManagers().getStorageManager().getAccount(player.getUniqueId()).thenAccept(account -> {
            account.addCompletedQuest(this);
            account.removeActiveQuest(this);
        });
        Bukkit.getPluginManager().callEvent(new QuestCompleteEvent(this, player));
    }

    private void giveEffectsAndRewards(Player player) {
        player.sendTitle(Translations.QUEST_COMPLETED_TITLE.get(player), getDisplayName(), 40, 60, 40);
        player.playSound(player.getLocation(),
                completeSound == null ? Sound.UI_TOAST_CHALLENGE_COMPLETE : completeSound, 1f, 1f);
        if (getPlugin().getConfig().getBoolean("settings.server.quest.completed.use_entity_totem_effect")) { // Use default effect
            player.playEffect(EntityEffect.TOTEM_RESURRECT);
        } else { // Use only particles
            player.getWorld().spawnParticle(Particle.TOTEM, player.getLocation(), 1000, 0.25, 0.25, 0.25, 1);
        }
        rewards.forEach(reward -> reward.award(player));
        Translations.QUEST_COMPLETED.sendList(player, getDisplayName());
    }

    /**
     * Attempts to start a quest for a player.
     * @param player player to start quest for
     * @return true if quest was started, false if player has already completed and cannot restart,
     *         has an active quest cooldown,
     *         or does not meet required quests beforehand
     */
    public CompletableFuture<DenyReason> tryStart(@NotNull Player player) {
        CompletableFuture<DenyReason> future = new CompletableFuture<>();
        getPlugin().getManagers().getStorageManager().getAccount(player.getUniqueId()).thenAccept(account -> {
            if (account.getActiveQuests().contains(this)) {
                future.complete(DenyReason.ALREADY_STARTED);
                return;
            }

            if (isCompleted(player) && !canRestart) {
                future.complete(DenyReason.CANNOT_RESTART);
                return;
            } else {
                long cdTime = TimeUtils.convertTicks(cooldown, TimeUnit.MILLISECONDS);
                if (System.currentTimeMillis() <= (account.getCompletedTime(this) + cdTime)) {
                    future.complete(DenyReason.COOLDOWN);
                    return;
                }
                account.removeCompletedQuest(this);
            }

            if (!testRequirements(player)) {
                future.complete(DenyReason.REQUIREMENTS_NOT_MET);
                return;
            }

            player.sendTitle(Translations.QUEST_STARTED.get(player), getDisplayName(), 40, 60, 40);
            if (account.getActiveQuests().contains(this)) account.removeActiveQuest(this);
            account.addActiveQuest(this);
            objectives.forEach(objective -> objective.setIncrement(player, 0));
            Bukkit.getPluginManager().callEvent(new QuestStartEvent(this, player));
            future.complete(DenyReason.NONE);
        }).exceptionally(err -> {
            err.printStackTrace();
            return null;
        });
        return future;
    }

    /**
     * Checks whether a player meets all requirements to start this quest.
     * @param player player to check
     * @return true if requirements are met, false otherwise
     */
    public boolean testRequirements(@NotNull Player player) {
        if (!getRequirements().isEmpty()) {
            for (QuestRequirement requirement : requirements) {
                if (!requirement.meetsRequirements(player)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void forceStart(@NotNull Player player) {
        getPlugin().getManagers().getStorageManager().getAccount(player.getUniqueId()).thenAccept(account -> {
            account.removeCompletedQuest(this);
            objectives.forEach(objective -> objective.setIncrement(player, 0));
            player.sendTitle(Translations.QUEST_STARTED.get(player), getName(), 40, 60, 40);
            account.addActiveQuest(this);
        }).exceptionally(err -> {
            err.printStackTrace();
            return null;
        });
    }

    public IQuesteAPI getPlugin() {
        if (plugin == null) this.plugin = QuesteAPI.getAPI();
        return plugin;
    }

    public boolean save(IQuesteAPI plugin) {
        File file = new File(plugin.getDataFolder() + "/quests/" + this.getName() + ".json");
        try {
            try (Writer writer = new FileWriter(file)) {
                Gson gson = plugin.getGson();
                try {
                    gson.toJson(this, writer);
                    writer.flush();
                } catch (Exception e) { // We're catching all exceptions so the file cannot be randomly wiped
                    plugin.getLogger().severe("Unable to save " + this.name + "! Please report this.");
                    e.printStackTrace();
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete() throws IOException {
        File file = new File(getPlugin().getDataFolder() + "/quests/" + this.getName() + ".json");
        return Files.deleteIfExists(file.toPath());
    }

    // Need to clean this up, find a better way...
    @Override
    public void add(IGuiEditable element) {
        if (element instanceof QuestRequirement requirement) {
            requirements.add(requirement);
        } else if (element instanceof QuestReward reward) {
            rewards.add(reward);
        } else if (element instanceof QuestStart start) {
            starters.add(start);
        } else if (element instanceof QuestObjective objective) {
            objectives.add(objective);
        }
    }

    @Override
    public void remove(IGuiEditable element) {
        if (element instanceof QuestRequirement requirement) {
            requirements.remove(requirement);
        } else if (element instanceof QuestReward reward) {
            rewards.remove(reward);
        } else if (element instanceof QuestStart start) {
            starters.remove(start);
        } else if (element instanceof QuestObjective objective) {
            objectives.remove(objective);
        }
    }

    public enum DenyReason {
        REQUIREMENTS_NOT_MET,
        CANNOT_RESTART,
        COOLDOWN,
        ALREADY_STARTED,
        NONE
    }
}
