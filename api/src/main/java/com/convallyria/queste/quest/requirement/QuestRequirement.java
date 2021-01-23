package com.convallyria.queste.quest.requirement;

import org.bukkit.entity.Player;

public abstract class QuestRequirement {

    public abstract boolean meetsRequirements(Player player);

    public abstract String getName();
}
