package com.convallyria.queste.gui;

import com.convallyria.queste.Queste;
import com.convallyria.queste.managers.registry.IQuesteRegistry;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.utils.ItemStackBuilder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AddQuestElementGUI extends QuesteGUI {

    private final Queste plugin;
    private final Player player;
    private final Quest quest;
    private final IQuesteRegistry<?> registry;
    private ChestGui gui;

    protected AddQuestElementGUI(Queste plugin, Player player, Quest quest, IQuesteRegistry<?> registry) {
        super(plugin, player);
        this.plugin = plugin;
        this.player = player;
        this.quest = quest;
        this.registry = registry;
    }

    @Override
    public void render() {
        this.gui = new ChestGui(6, quest.getName());
        gui.setOnGlobalClick(click -> click.setCancelled(true));

        PaginatedPane pane = super.generateDefaultConfig();
        for (GuiItem item : getExitPane().getItems()) { // Set exit pane action
            item.setAction(event -> {
                new QuestCreateGUI(plugin, player, quest).open();
                player.playSound(player.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1f, 1f);
            });
        }

        List<GuiItem> items = new ArrayList<>();
        registry.get().forEach((name, clazz) -> {
            ItemStack item = new ItemStackBuilder(Material.WRITTEN_BOOK).withName("&6" + name).generation(null).build();
            GuiItem guiItem = new GuiItem(item, click -> {
                Object newInstance = registry.getNew(name, plugin, quest);
                if (newInstance == null) {
                    player.sendMessage(ChatColor.RED + "This requires a plugin which is not installed.");
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1f, 1f);
                    return;
                }

                quest.add((IGuiEditable) newInstance);

                new EditQuestElementGUI(plugin, player, quest, registry).open();
                player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f);
            });
            items.add(guiItem);
        });
        pane.populateWithGuiItems(items);
        gui.update();
    }

    @Override
    public ChestGui getGui() {
        return gui;
    }
}
