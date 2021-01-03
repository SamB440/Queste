package com.convallyria.queste.quest.reward;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Reward to send messages to the player. More complex messages can use the tellraw command in {@link ConsoleCommandReward}.
 */
public final class MessageReward extends QuestReward {

    private final List<String> messages;

    public MessageReward(List<String> messages) {
        this.messages = messages;
    }

    @Override
    public void award(Player player) {
        messages.forEach(message -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
    }

    @Override
    public String getName() {
        return "Message";
    }
}
