package com.convallyria.queste.task;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.quest.objective.QuestObjective;
import com.convallyria.queste.translation.Translations;
import com.convallyria.queste.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class UpdateBossbarTask implements Runnable {

    private final Queste plugin;

    public UpdateBossbarTask(Queste plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            plugin.getManagers().getStorageManager().getAccount(player.getUniqueId()).thenAccept(account -> {
                for (Quest quest : account.getActiveQuests()) {
                    BossBar bossBar = getOrDefaultBossBar(quest, player, true);
                    QuestObjective currentObjective = quest.getCurrentObjective(player);
                    if (currentObjective != null && !quest.isCompleted(player)) {
                        if (!bossBar.getPlayers().contains(player)) bossBar.addPlayer(player);
                        int increment = currentObjective.getIncrement(player);
                        float percent = (increment * 100.0f) / currentObjective.getCompletionAmount();
                        if (quest.getTime() == 0)
                            bossBar.setTitle(Translations.OBJECTIVE_PROGRESS.get(player, currentObjective.getDisplayName(), increment, currentObjective.getCompletionAmount()));
                        else {
                            long time = TimeUtils.convertTicks(quest.getTime(), TimeUnit.MILLISECONDS);
                            long startTime = account.getStartTime(quest);
                            long timeLeft = TimeUtils.convert((startTime + time) - System.currentTimeMillis(), TimeUnit.MILLISECONDS, TimeUnit.SECONDS);
                            bossBar.setTitle(Translations.OBJECTIVE_PROGRESS.get(player, currentObjective.getDisplayName() + ChatColor.GRAY + " (" + timeLeft + "s)", increment, currentObjective.getCompletionAmount()));
                        }
                        bossBar.setProgress(percent / 100);
                    } else {
                        bossBar.removeAll();
                        Advancement advancement = Bukkit.getAdvancement(quest.getKey());
                        AdvancementProgress progress = player.getAdvancementProgress(advancement);
                        progress.revokeCriteria("impossible");
                    }
                }

                for (Quest quest : plugin.getManagers().getQuesteCache().getQuests().values()) {
                    if (!account.getActiveQuests().contains(quest)) {
                        BossBar bossBar = Bukkit.getBossBar(getKey(player, quest));
                        if (bossBar != null) {
                            bossBar.removeAll();
                        }
                    }
                }
            });
        }
    }

    public BossBar getOrDefaultBossBar(Quest quest, Player player, boolean create) {
        QuestObjective currentObjective = quest.getCurrentObjective(player);
        BossBar activeBar = Bukkit.getBossBar(getKey(player, quest));
        if (activeBar == null && create) {
            if (currentObjective == null) return null;
            activeBar = Bukkit.createBossBar(getKey(player, quest),
                    Translations.OBJECTIVE_PROGRESS.get(player, currentObjective.getDisplayName(),
                            currentObjective.getIncrement(player), currentObjective.getCompletionAmount()),
                    BarColor.WHITE, BarStyle.SEGMENTED_10);
            activeBar.addPlayer(player);
        }
        return activeBar;
    }

    private NamespacedKey getKey(Player player, Quest quest) {
        return new NamespacedKey(plugin, player.getUniqueId() + quest.getName());
    }
}
