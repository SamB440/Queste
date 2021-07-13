package com.convallyria.queste.chat.preset;

import com.convallyria.queste.Queste;
import com.convallyria.queste.chat.QuesteConversationPrefix;
import com.convallyria.queste.chat.QuesteStringPrompt;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public class ReturnValueConversationPreset {

    public ReturnValueConversationPreset(Player player, String question, Consumer<String> action) {
        Queste plugin = JavaPlugin.getPlugin(Queste.class);
        ConversationFactory factory = new ConversationFactory(plugin)
                .withModality(true)
                .withPrefix(new QuesteConversationPrefix())
                .withFirstPrompt(new QuesteStringPrompt(question))
                .withEscapeSequence("quit")
                .withLocalEcho(true)
                .withTimeout(60);
        Conversation conversation = factory.buildConversation(player);
        conversation.begin();
        conversation.addConversationAbandonedListener(abandonedEvent -> {
            String input = (String) abandonedEvent.getContext().getSessionData("input");
            if (input == null) return;
            action.accept(input);
        });
        player.closeInventory();
    }
}
