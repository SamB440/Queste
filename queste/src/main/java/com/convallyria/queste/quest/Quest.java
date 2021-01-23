package com.convallyria.queste.quest;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.objective.QuestObjective;
import com.convallyria.queste.quest.requirement.QuestRequirement;
import com.convallyria.queste.quest.reward.QuestReward;
import com.convallyria.queste.translation.Translations;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class Quest implements Keyed {

    private transient Queste plugin;
    private final String name;
    private String displayName;
    private boolean canRestart;
    private final List<QuestObjective> objectives;
    private final List<QuestReward> rewards;
    private final List<QuestRequirement> requirements;
    private boolean storyMode;
    private Sound completeSound;
    private int time;
    private Material icon;
    private String description;

    public Quest(@NotNull String name) {
        this.name = name;
        this.objectives = new ArrayList<>();
        this.rewards = new ArrayList<>();
        this.requirements = new ArrayList<>();
        this.completeSound = Sound.UI_TOAST_CHALLENGE_COMPLETE;
        this.icon = Material.TOTEM_OF_UNDYING;
        this.description = "A quest of great honour!";
    }

    @Override
    @NotNull
    public NamespacedKey getKey() {
        return new NamespacedKey(getPlugin(), getSafeName() + "/root");
    }

    public String getName() {
        return name;
    }

    public String getSafeName() {
        return getName().replace(" ", "_");
    }

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

    public void addObjective(QuestObjective objective) {
        objectives.add(objective);
    }

    public void removeObjective(QuestObjective objective) {
        objectives.remove(objective);
    }

    public List<QuestObjective> getObjectives() {
        return objectives;
    }

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
     * Checks if a player has completed this quest.
     * @param player player to check
     * @return true if all quest objectives are completed, false otherwise
     */
    public boolean isCompleted(@NotNull Player player) {
        boolean objectivesCompleted = true;
        for (QuestObjective objective : objectives) {
            if (!objective.hasCompleted(player)) {
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
            Advancement advancement = Bukkit.getAdvancement(getKey());
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            progress.awardCriteria("impossible");
        });
    }

    private void giveEffectsAndRewards(Player player) {
        player.sendTitle(Translations.QUEST_COMPLETED_TITLE.get(player), getName(), 40, 60, 40);
        player.playSound(player.getLocation(),
                completeSound == null ? Sound.UI_TOAST_CHALLENGE_COMPLETE : completeSound, 1f, 1f);
        player.getWorld().spawnParticle(Particle.TOTEM, player.getLocation(), 1000, 0.25, 0.25, 0.25, 1);
        rewards.forEach(reward -> reward.award(player));
        Translations.QUEST_COMPLETED.sendList(player, getDisplayName());
    }

    /**
     * Attempts to start a quest for a player.
     * @param player player to start quest for
     * @return true if quest was started, false if player has already completed and cannot restart
     *         or does not meet required quests beforehand
     */
    public CompletableFuture<DenyReason> tryStart(@NotNull Player player) {
        CompletableFuture<DenyReason> future = new CompletableFuture<>();
        getPlugin().getManagers().getStorageManager().getAccount(player.getUniqueId()).thenAccept(account -> {
            if (isCompleted(player) && !canRestart) {
                future.complete(DenyReason.CANNOT_RESTART);
                return;
            }

            if (!testRequirements(player)) {
                future.complete(DenyReason.REQUIREMENTS_NOT_MET);
                return;
            }

            objectives.forEach(objective -> objective.setIncrement(player, 0));
            player.sendTitle(Translations.QUEST_STARTED.get(player), getName(), 40, 60, 40);
            if (account.getActiveQuests().contains(this)) account.removeActiveQuest(this);
            account.addActiveQuest(this);
            if (plugin.getConfig().getBoolean("settings.server.advancements.generate")) {
                Advancement advancement = Bukkit.getAdvancement(getKey());
                AdvancementProgress progress = player.getAdvancementProgress(advancement);
                progress.awardCriteria("impossible");
            }
            future.complete(DenyReason.NONE);
        }).exceptionally(err -> {
            err.printStackTrace();
            return null;
        });
        return future;
    }

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
            objectives.forEach(objective -> objective.setIncrement(player, 0));
            player.sendTitle(Translations.QUEST_STARTED.get(player), getName(), 40, 60, 40);
            account.addActiveQuest(this);
        }).exceptionally(err -> {
            err.printStackTrace();
            return null;
        });
    }

    @NotNull
    public Queste getPlugin() {
        if (plugin == null) this.plugin = JavaPlugin.getPlugin(Queste.class);
        return plugin;
    }

    public boolean save(Queste plugin) {
        File file = new File(plugin.getDataFolder() + "/quests/" + this.getName() + ".json");
        try {
            Writer writer = new FileWriter(file);
            Gson gson = plugin.getGson();
            gson.toJson(this, writer);
            writer.flush();
            writer.close();
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

    public enum DenyReason {
        REQUIREMENTS_NOT_MET,
        CANNOT_RESTART,
        NONE
    }
}