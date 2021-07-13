package com.convallyria.queste.chat.preset;

import com.convallyria.queste.Queste;
import com.convallyria.queste.chat.QuesteConversationPrefix;
import com.convallyria.queste.chat.QuesteStringPrompt;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public class ConfirmConversationPreset {

    public ConfirmConversationPreset(Player player, Consumer<Boolean> action) {
        Queste plugin = JavaPlugin.getPlugin(Queste.class);
        ConversationFactory factory = new ConversationFactory(plugin)
                .withModality(true)
                .withPrefix(new QuesteConversationPrefix())
                .withFirstPrompt(new QuesteStringPrompt("Are you sure you want to do this? (yes/no)"))
                .withEscapeSequence("quit")
                .withLocalEcho(true)
                .withTimeout(60);
        Conversation conversation = factory.buildConversation(player);
        conversation.begin();
        conversation.addConversationAbandonedListener(abandonedEvent -> {
            String input = (String) abandonedEvent.getContext().getSessionData("input");
            if (input == null) return;
            boolean accepted = input.equalsIgnoreCase("yes");
            action.accept(accepted);
        });
        player.closeInventory();
    }
}
