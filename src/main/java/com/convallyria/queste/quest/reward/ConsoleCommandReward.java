package com.convallyria.queste.quest.reward;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ConsoleCommandReward extends QuestReward {

	private final String command;

	public ConsoleCommandReward(String command) {
		this.command = command;
	}

	@Override
	public void award(Player player) {
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
	}

	@Override
	public String getName() {
		return "Console Command";
	}
}