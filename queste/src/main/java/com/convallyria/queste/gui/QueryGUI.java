package com.convallyria.queste.gui;

import com.convallyria.queste.Queste;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.entity.Player;

import java.util.List;

public class QueryGUI extends QuesteGUI {

    private final List<GuiItem> items;
    private ChestGui gui;

    public QueryGUI(Queste plugin, Player player, List<GuiItem> items) {
        super(plugin, player);
        this.items = items;
    }

    @Override
    public void render() {
        this.gui = new ChestGui(1, "What would you like to do?");
        gui.setOnGlobalClick(click -> click.setCancelled(true));

        StaticPane options = new StaticPane(0, 0, 9, 1);
        int x = 0;
        for (GuiItem item : items) {
            options.addItem(item, x, 0);
            x++;
        }
        gui.addPane(options);
        gui.update();
    }

    @Override
    public ChestGui getGui() {
        return gui;
    }
}
