package com.convallyria.queste.quest.reward;

import org.bukkit.entity.Player;

public abstract class QuestReward {

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
