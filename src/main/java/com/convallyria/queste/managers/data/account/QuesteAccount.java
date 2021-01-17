package com.convallyria.queste.managers.data.account;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.quest.objective.QuestObjective;
import com.convallyria.queste.translation.Translations;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QuesteAccount {

    private transient Map<String, Integer> time = new HashMap<>();
    private transient Map<String, Integer> tasks = new HashMap<>();
    private final UUID uuid;
    private final List<Quest> activeQuests;
    private final List<Quest> completedQuests;

    public QuesteAccount(UUID uuid) {
        this.uuid = uuid;
        this.activeQuests = new ArrayList<>();
        this.completedQuests = new ArrayList<>();
    }

    public ImmutableList<Quest> getActiveQuests() {
        return ImmutableList.copyOf(activeQuests);
    }

    public ImmutableList<Quest> getCompletedQuests() {
        return ImmutableList.copyOf(completedQuests);
    }

    public void addActiveQuest(Quest quest) {
        activeQuests.add(quest);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            QuestObjective currentObjective = quest.getCurrentObjective(player);
            if (currentObjective != null) {
                Queste plugin = JavaPlugin.getPlugin(Queste.class);
                BossBar activeBar = Bukkit.getBossBar(new NamespacedKey(plugin, player.getUniqueId() + quest.getName()));
                if (activeBar == null) {
                    activeBar = Bukkit.createBossBar(new NamespacedKey(plugin, player.getUniqueId() + quest.getName()),
                            Translations.OBJECTIVE_PROGRESS.get(player, currentObjective.getDisplayName(),
                                    currentObjective.getIncrement(player), currentObjective.getCompletionAmount()),
                            BarColor.WHITE, BarStyle.SEGMENTED_10);
                }
                activeBar.setProgress(0);
                activeBar.addPlayer(player);
                update(quest);

                if (quest.getTime() > 0) {
                    time.put(quest.getName(), quest.getTime());
                    tasks.put(quest.getName(), Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                        int timeLeft = time.get(quest.getName());
                        time.replace(quest.getName(), timeLeft - 1);
                        BossBar bossBar = Bukkit.getBossBar(new NamespacedKey(plugin, player.getUniqueId() + quest.getName()));
                        if (bossBar == null) return;
                        bossBar.setTitle(Translations.OBJECTIVE_PROGRESS.get(player, currentObjective.getDisplayName(),
                                currentObjective.getIncrement(player), currentObjective.getCompletionAmount()) + ChatColor.GRAY + " (" + timeLeft + "s)");
                        if (timeLeft <= 0) {
                            Bukkit.getScheduler().cancelTask(tasks.get(quest.getName()));
                            time.remove(quest.getName());
                            tasks.remove(quest.getName());
                            if (!quest.isCompleted(player)) {
                                activeQuests.remove(quest);
                                bossBar.removeAll();
                                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1f, 1f);
                                player.sendTitle(Translations.QUEST_FAILED_TITLE.get(player), quest.getDisplayName(), 40, 60, 40);
                            }
                        }
                    }, 0L, 20L));
                }
            }
        }
    }

    public void removeActiveQuest(Quest quest) {
        activeQuests.remove(quest);
        update(quest);
    }

    public void removeCompletedQuest(Quest quest) {
        completedQuests.remove(quest);
    }

    public void update(Quest quest) {
        Queste plugin = JavaPlugin.getPlugin(Queste.class);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            BossBar bossBar = Bukkit.getBossBar(new NamespacedKey(plugin, player.getUniqueId() + quest.getName()));
            if (bossBar == null) return;
            QuestObjective currentObjective = quest.getCurrentObjective(player);
            if (currentObjective != null) {
                int increment = currentObjective.getIncrement(player);
                float percent = (increment * 100.0f) / currentObjective.getCompletionAmount();
                if (quest.getTime() == 0)
                    bossBar.setTitle(Translations.OBJECTIVE_PROGRESS.get(player, currentObjective.getDisplayName(), increment, currentObjective.getCompletionAmount()));
                bossBar.setProgress(percent / 100);
            } else {
                bossBar.removeAll();
            }
        }
    }

    public void addCompletedQuest(Quest quest) {
        completedQuests.add(quest);
    }
}