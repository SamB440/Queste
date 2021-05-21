package com.convallyria.queste.utils;

import com.convallyria.queste.chat.QuesteConversationPrefix;
import com.convallyria.queste.chat.QuesteStringPrompt;
import net.islandearth.rpgregions.api.RPGRegionsAPI;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ConversationUtils {

    public static Conversation getNewConversation(Player player, boolean begin) {
        ConversationFactory factory = new ConversationFactory((Plugin) RPGRegionsAPI.getAPI())
                .withModality(true)
                .withPrefix(new QuesteConversationPrefix())
                .withFirstPrompt(new QuesteStringPrompt("Enter value:"))
                .withEscapeSequence("quit")
                .withLocalEcho(true)
                .withTimeout(60);
        Conversation conversation = factory.buildConversation(player);
        if (begin) conversation.begin();
        return conversation;
    }
}
