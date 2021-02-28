package com.convallyria.queste.quest.requirement;

import com.convallyria.queste.gui.IGuiEditable;
import org.bukkit.entity.Player;

public abstract class QuestRequirement implements IGuiEditable {

    /**
     * Checks if this player meets the requirements
     * @param player player to check
     * @return true if player meets requirements
     */
    public abstract boolean meetsRequirements(Player player);
}
