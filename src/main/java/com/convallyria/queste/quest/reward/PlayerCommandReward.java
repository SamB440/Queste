package com.convallyria.queste.quest.reward;

import org.bukkit.entity.Player;

public final class PlayerCommandReward extends QuestReward {

	private final String command;

	public PlayerCommandReward(String command) {
		this.command = command;
	}

	@Override
	public void award(Player player) {
		player.performCommand(command.replace("%player%", player.getName()));
	}

	@Override
	public String getName() {
		return "Player Command";
	}
}