package com.convallyria.queste.quest.reward;

import com.convallyria.queste.Queste;
import org.bukkit.entity.Player;

public final class PlayerCommandReward extends QuestReward {

	private final String command;

	public PlayerCommandReward(Queste plugin) {
		super(plugin);
		this.command = "me executed this command";
	}

	public PlayerCommandReward(Queste plugin, String command) {
		super(plugin);
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