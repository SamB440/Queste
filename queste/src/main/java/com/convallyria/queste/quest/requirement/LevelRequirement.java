package com.convallyria.queste.quest.requirement;

import com.convallyria.queste.gui.GuiEditable;
import org.bukkit.entity.Player;

public final class LevelRequirement extends QuestRequirement {

    @GuiEditable("Level")
    private final int level;

    public LevelRequirement() {
        this.level = 1;
    }

    public int getLevel() {
        return level;
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
