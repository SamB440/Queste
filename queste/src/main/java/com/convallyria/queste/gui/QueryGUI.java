package com.convallyria.queste.gui;

import com.convallyria.queste.Queste;
import com.convallyria.queste.managers.registry.IQuesteRegistry;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.utils.ItemStackBuilder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.islandearth.rpgregions.utils.XSound;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class QueryGUI extends QuesteGUI {

    private final List<GuiItem> items;
    private ChestGui gui;

    public QueryGUI(Queste plugin, Player player, Quest quest, IQuesteRegistry<?> registry) {
        super(plugin, player);
        this.items = new ArrayList<>();
        ItemStack add = new ItemStackBuilder(Material.WRITABLE_BOOK)
                .withName("&6Add " + registry.getRegistryName())
                .build();
        GuiItem addRegistryGuiItem = new GuiItem(add, click -> {
            new AddQuestElementGUI(plugin, player, quest, registry).open();
            XSound.ITEM_BOOK_PAGE_TURN.play(player, 1f, 1f);
        });
        items.add(addRegistryGuiItem);

        ItemStack edit = new ItemStackBuilder(Material.WRITABLE_BOOK)
                .withName("&6Edit " + registry.getRegistryName())
                .build();
        GuiItem editRegistryGuiItem = new GuiItem(edit, click -> {
            new EditQuestElementGUI(plugin, player, quest, registry).open();
            XSound.ITEM_BOOK_PAGE_TURN.play(player, 1f, 1f);
        });
        items.add(editRegistryGuiItem);
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
