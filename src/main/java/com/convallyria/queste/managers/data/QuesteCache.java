package com.convallyria.queste.managers.data;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QuesteCache {

	private final Queste plugin;
	private final Map<String, Quest> quests;

	public QuesteCache(Queste plugin) {
		this.plugin = plugin;
		this.quests = new ConcurrentHashMap<>();
	}

	public ImmutableMap<String, Quest> getQuests() {
		return ImmutableMap.copyOf(quests);
	}

	public void addQuest(Quest quest) {
		quests.put(quest.getName(), quest);
	}

	public void removeQuest(Quest quest) {
		quests.remove(quest.getName());
	}
}
