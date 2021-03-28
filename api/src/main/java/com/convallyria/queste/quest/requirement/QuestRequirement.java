package com.convallyria.queste.quest.requirement;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.gui.IGuiEditable;
import org.bukkit.entity.Player;

public abstract class QuestRequirement implements IGuiEditable {

    private final transient IQuesteAPI api;

    public QuestRequirement(IQuesteAPI api) {
        this.api = api;
    }

    public IQuesteAPI getAPI() {
        return api;
    }

    /**
     * Checks if this player meets the requirements
     * @param player player to check
     * @return true if player meets requirements
     */
    public abstract boolean meetsRequirements(Player player);
}
