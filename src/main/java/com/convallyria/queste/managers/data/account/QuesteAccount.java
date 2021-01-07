package com.convallyria.queste.managers.data.account;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.quest.objective.QuestObjective;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuesteAccount {

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
							ChatColor.GOLD + currentObjective.getType().getName(),
							BarColor.WHITE, BarStyle.SEGMENTED_10);
				}
				activeBar.setProgress(0);
				activeBar.addPlayer(player);
				update(quest);
			}
		}
	}

	public void removeActiveQuest(Quest quest) {
		activeQuests.remove(quest);
		update(quest);
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
				bossBar.setTitle(ChatColor.GOLD + currentObjective.getType().getName());
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