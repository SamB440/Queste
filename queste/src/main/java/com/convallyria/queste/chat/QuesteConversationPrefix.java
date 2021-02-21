package com.convallyria.queste.chat;

import com.convallyria.queste.colour.ColourScheme;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationPrefix;
import org.jetbrains.annotations.NotNull;

public class QuesteConversationPrefix implements ConversationPrefix {

    @NotNull
    @Override
    public String getPrefix(@NotNull ConversationContext context) {
        return ColourScheme.getPrimaryColour() + "Queste > " + ColourScheme.getSecondaryColour();
    }
}
