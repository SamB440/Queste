package com.convallyria.queste.quest.reward;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public abstract class QuestReward implements Cloneable {

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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public enum QuestRewardEnum {
        CONSOLE_COMMAND(new ConsoleCommandReward("say example command!")),
        EXPERIENCE(new ExperienceReward(1)),
        ITEM(new ItemReward(new ItemStack(Material.WOODEN_SWORD))),
        MESSAGE(new MessageReward(Arrays.asList("a message!", "another message!"))),
        MONEY(new MoneyReward(1)),
        PLAYER_COMMAND(new PlayerCommandReward("me executed this command"));

        private final QuestReward reward;

        QuestRewardEnum(QuestReward reward) {
            this.reward = reward;
        }

        @Nullable
        public QuestReward getReward() {
            try {
                return (QuestReward) reward.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
