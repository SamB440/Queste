package com.convallyria.queste.quest.requirement;

import com.convallyria.queste.gui.IGuiEditable;
import org.bukkit.entity.Player;

public abstract class QuestRequirement implements IGuiEditable {

    /**
     * Checks if this player meets the requirements
     * @param player player to check
     */
    public abstract boolean meetsRequirements(Player player);

    /**
     * User friendly name of this reward.
     * @return name of reward
     */
    public abstract String getName();
}
