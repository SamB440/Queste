package com.convallyria.queste.gui;

import com.convallyria.queste.Queste;
import org.bukkit.entity.Player;

public abstract class QuesteGUI {

    private final Queste plugin;
    private final Player player;

    public QuesteGUI(Queste plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public abstract void open();
}
