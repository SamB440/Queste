package com.convallyria.queste.quest.start;

import com.convallyria.queste.Queste;
import org.bukkit.entity.Player;

public abstract class QuestRequirement {

    private transient final Queste plugin;

    protected QuestRequirement(Queste plugin) {
        this.plugin = plugin;
    }

    public Queste getPlugin() {
        return plugin;
    }

    public abstract boolean meetsRequirements(Player player);

    public abstract String getName();
}