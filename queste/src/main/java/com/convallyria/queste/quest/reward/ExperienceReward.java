package com.convallyria.queste.quest.reward;

import com.convallyria.queste.gui.GuiEditable;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public final class ExperienceReward extends QuestReward {

	@GuiEditable(value = "Experience", icon = Material.EXPERIENCE_BOTTLE)
	private final int xp;

	public ExperienceReward() {
		this.xp = 10;
	}

	public ExperienceReward(int xp) {
		this.xp = xp;
	}
	
	@Override
	public void award(Player player) {
		player.giveExp(xp);
	}

	@Override
	public String getName() {
		return "Experience";
	}
}
