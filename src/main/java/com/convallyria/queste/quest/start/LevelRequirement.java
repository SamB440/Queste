package com.convallyria.queste.quest.start;

import com.convallyria.queste.Queste;
import org.bukkit.entity.Player;

public final class LevelRequirement extends QuestRequirement {

    private final int level;

    public LevelRequirement(Queste plugin) {
        super(plugin);
        this.level = 1;
    }

    @Override
    public boolean meetsRequirements(Player player) {
        return player.getLevel() >= 1;
    }

    @Override
    public String getName() {
        return "Level";
    }
}
