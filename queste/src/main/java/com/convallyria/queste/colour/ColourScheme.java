package com.convallyria.queste.colour;

import net.md_5.bungee.api.ChatColor;

public final class ColourScheme {

    private ColourScheme() {}

    public static ChatColor getPrimaryColour() {
        return ChatColor.of("#009DFF");
    }

    public static ChatColor getSecondaryColour() {
        return ChatColor.of("#F98900");
    }

    public static ChatColor getExternalPluginColour() {
        return ChatColor.of("#FF4800");
    }
}
