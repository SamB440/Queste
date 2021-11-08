package com.convallyria.queste.quest.requirement;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.gui.GuiEditable;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public final class LevelRequirement extends QuestRequirement {

    @GuiEditable(value = "Level", icon = Material.EXPERIENCE_BOTTLE)
    private final int level;

    public LevelRequirement(IQuesteAPI api) {
        super(api);
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
