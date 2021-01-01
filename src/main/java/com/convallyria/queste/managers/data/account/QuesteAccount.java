package com.convallyria.queste.managers.data.account;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.UUID;

public class QuesteAccount {

	private UUID uuid;
	private List<Quest> activeQuests;

	public QuesteAccount(UUID uuid) {
		this.uuid = uuid;
	}

	public ImmutableList<Quest> getActiveQuests() {
		return ImmutableList.copyOf(activeQuests);
	}

	public void addActiveQuest(Quest quest) {
		activeQuests.add(quest);
	}

	public boolean save(Queste plugin) {
		File file = new File(plugin.getDataFolder() + "/accounts/" + this.uuid + ".json");
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