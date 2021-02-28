package com.convallyria.queste.quest.reward;

import com.convallyria.queste.gui.IGuiEditable;
import org.bukkit.entity.Player;

public abstract class QuestReward implements IGuiEditable {

    /**
     * Awards this reward to the specified player
     * @param player player to award to
     */
    public abstract void award(Player player);
}
