package com.convallyria.queste.quest.reward;

import com.convallyria.queste.Queste;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ConsoleCommandReward extends QuestReward {

	private final String command;

	public ConsoleCommandReward(Queste plugin) {
		super(plugin);
		this.command = "say example command!";
	}

	public ConsoleCommandReward(Queste plugin, String command) {
		super(plugin);
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