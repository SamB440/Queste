package com.convallyria.queste.quest.reward;

import com.convallyria.queste.Queste;
import org.bukkit.entity.Player;

public abstract class QuestReward {

    private transient final Queste plugin;

    protected QuestReward(Queste plugin) {
        this.plugin = plugin;
    }

    public Queste getPlugin() {
        return plugin;
    }

    /**
     * Awards this reward to the specified player
     * @param player player to award to
     */
    public abstract void award(Player player);

    /**
     * User friendly name of this reward.
     * @return name of reward
     */
    public abstract String getName();
}
