package com.convallyria.queste.quest.reward;

import org.bukkit.entity.Player;

public final class ExperienceReward extends QuestReward {
	
	private final int xp;
	
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
