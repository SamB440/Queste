package com.convallyria.queste.quest.requirement;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.gui.GuiEditable;
import org.bukkit.entity.Player;

public final class PermissionRequirement extends QuestRequirement {

    @GuiEditable("Permission")
    private String permission;

    public PermissionRequirement(IQuesteAPI api) {
        super(api);
    }

    @Override
    public boolean meetsRequirements(Player player) {
        return player.hasPermission(permission);
    }

    @Override
    public String getName() {
        return "Permission";
    }
}
