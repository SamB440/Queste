package com.convallyria.queste.gui;

import com.convallyria.queste.Queste;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import org.bukkit.entity.Player;

public class DefaultGUI extends QuesteGUI {

    private ChestGui gui;
    private PaginatedPane pane;

    public DefaultGUI(Queste plugin, Player player) {
        super(plugin, player);
    }

    @Override
    public void render() {
        this.gui = new ChestGui(6, "Default");
        gui.setOnGlobalClick(click -> click.setCancelled(true));
        this.pane = super.generateDefaultConfig();
    }

    public PaginatedPane getPane() {
        return pane;
    }

    @Override
    public ChestGui getGui() {
        return gui;
    }
}
