package com.convallyria.queste.quest.reward;

import com.convallyria.queste.Queste;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Reward to send messages to the player. More complex messages can use the tellraw command in {@link ConsoleCommandReward}.
 */
public final class MessageReward extends QuestReward {

    private final List<String> messages;

    public MessageReward(Queste plugin) {
        super(plugin);
        this.messages = Arrays.asList("Message one!", "Message two!");
    }

    public MessageReward(Queste plugin, List<String> messages) {
        super(plugin);
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
