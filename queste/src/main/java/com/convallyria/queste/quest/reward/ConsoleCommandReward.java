package com.convallyria.queste.quest.reward;

import com.convallyria.queste.gui.GuiEditable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ConsoleCommandReward extends QuestReward {

	@GuiEditable("Command")
	private final String command;

	public ConsoleCommandReward() {
		this.command = "say example command!";
	}

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