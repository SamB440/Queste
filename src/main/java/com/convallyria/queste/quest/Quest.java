package com.convallyria.queste.quest;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.objective.QuestObjective;
import com.convallyria.queste.quest.reward.QuestReward;
import com.convallyria.queste.translation.Translations;
import com.google.gson.Gson;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class Quest  {

    private transient Queste plugin;
    private final String name;
    private String displayName;
    private boolean canRestart;
    private final List<QuestObjective> objectives;
    private final List<Quest> requiredQuests;
    private final List<QuestReward> rewards;
    private boolean storyMode;
    private Sound completeSound;

    public Quest(String name) {
        this.name = name;
        this.objectives = new ArrayList<>();
        this.requiredQuests = new ArrayList<>();
        this.rewards = new ArrayList<>();
        this.completeSound = Sound.UI_TOAST_CHALLENGE_COMPLETE;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
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
        List<QuestObjective> objectives = new ArrayList<>();
        for (QuestObjective objective : getObjectives()) {
            if (objective.getClass().isInstance(type)) {
                objectives.add(objective);
            }
        }
        return objectives;
    }

    public void addRequiredQuest(Quest quest) {
        requiredQuests.add(quest);
    }

    public void removeRequiredQuest(Quest quest) {
        requiredQuests.remove(quest);
    }

    public List<Quest> getRequiredQuests() {
        return requiredQuests;
    }

    public void addReward(QuestReward reward) {
        rewards.add(reward);
    }

    public void removeReward(QuestReward reward) {
        rewards.remove(reward);
    }

    public List<QuestReward> getRewards() {
        return rewards;
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
    public CompletableFuture<Boolean> tryStart(@NotNull Player player) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        getPlugin().getManagers().getStorageManager().getAccount(player.getUniqueId()).thenAccept(account -> {
            if (isCompleted(player) && !canRestart) {
                future.complete(false);
                return;
            }

            if (!getRequiredQuests().isEmpty()) {
                for (Quest requiredQuest : requiredQuests) {
                    if (!account.getCompletedQuests().contains(requiredQuest)) {
                        future.complete(false);
                        return;
                    }
                }
            }

            objectives.forEach(objectives -> objectives.setIncrement(player, 0));
            player.sendTitle(Translations.QUEST_STARTED.get(player), getName(), 40, 60, 40);
            if (account.getActiveQuests().contains(this)) account.removeActiveQuest(this);
            account.addActiveQuest(this);
            future.complete(true);
        }).exceptionally(err -> {
            err.printStackTrace();
            return null;
        });
        return future;
    }

    public void forceStart(@NotNull Player player) {
        getPlugin().getManagers().getStorageManager().getAccount(player.getUniqueId()).thenAccept(account -> {
            objectives.forEach(objectives -> objectives.setIncrement(player, 0));
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
}
