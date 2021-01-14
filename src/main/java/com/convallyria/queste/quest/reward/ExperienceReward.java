package com.convallyria.queste.quest.reward;

import com.convallyria.queste.Queste;
import org.bukkit.entity.Player;

public final class ExperienceReward extends QuestReward {
	
	private final int xp;

	public ExperienceReward(Queste plugin) {
		super(plugin);
		this.xp = 10;
	}

	public ExperienceReward(Queste plugin, int xp) {
		super(plugin);
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
