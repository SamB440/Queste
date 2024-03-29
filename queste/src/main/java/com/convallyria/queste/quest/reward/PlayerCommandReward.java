package com.convallyria.queste.quest.reward;

import com.convallyria.queste.gui.GuiEditable;
import org.bukkit.entity.Player;

public final class PlayerCommandReward extends QuestReward {

	@GuiEditable("Command")
	private final String command;

	public PlayerCommandReward() {
		this.command = "me executed this command";
	}

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