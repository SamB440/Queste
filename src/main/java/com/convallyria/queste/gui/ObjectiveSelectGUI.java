package com.convallyria.queste.gui;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.quest.objective.QuestObjective;
import com.convallyria.queste.utils.ItemStackBuilder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObjectiveSelectGUI extends QuesteGUI {

    private final ChestGui gui;

    public ObjectiveSelectGUI(Queste plugin, Player player, Quest quest, Class<? extends QuestObjective> type, Method method, Object... data) {
        super(plugin, player);
        this.gui = new ChestGui(6, quest.getDisplayName());
        gui.setOnGlobalClick(click -> click.setCancelled(true));

        PaginatedPane pane = new PaginatedPane(1, 1, 7, 4);
        StaticPane back = new StaticPane(0, 5, 1, 1);
        StaticPane forward = new StaticPane(8, 5, 1, 1);
        StaticPane exit = new StaticPane(4, 5, 1, 1);
        List<QuestObjective> objectives = quest.getObjectivesFromType(type);

        // Back item
        ItemStack backItem = new ItemStackBuilder(Material.ARROW)
                        .withName(ChatColor.RED + "Previous Page")
                        .withLore(ChatColor.GRAY + "Go to the previous page.")
                        .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                        .build();

        back.addItem(new GuiItem(backItem, event -> {
            event.setCancelled(true);
            if (pane.getPages() == 0 || pane.getPages() == 1) return;

            pane.setPage(pane.getPage() - 1);

            forward.setVisible(true);
            gui.update();
        }), 0, 0);

        // Forward item
        ItemStack forwardItem = new ItemStackBuilder(Material.ARROW)
                .withName(ChatColor.GREEN + "Next Page")
                .withLore(ChatColor.GRAY + "Go to the next page.")
                .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                .build();

        forward.addItem(new GuiItem(forwardItem, event -> {
            event.setCancelled(true);
            if (pane.getPages() == 0 || pane.getPages() == 1) return;

            pane.setPage(pane.getPage() + 1);

            back.setVisible(true);
            gui.update();
        }), 0, 0);

        // Exit item
        ItemStack item = new ItemStackBuilder(Material.BARRIER)
                .withName(ChatColor.RED + "Exit")
                .withLore(ChatColor.GRAY + "Exit the GUI.")
                .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                .build();
        exit.addItem(new GuiItem(item, event -> {
            gui.update();
            player.closeInventory();
            quest.save(plugin);
        }), 0, 0);

        gui.addPane(exit);
        gui.addPane(back);
        gui.addPane(forward);

        List<GuiItem> guiItems = new ArrayList<>();
        for (QuestObjective objective : objectives) {
            ItemStack itemStack = new ItemStackBuilder(Material.TOTEM_OF_UNDYING)
                    .withName(ChatColor.GREEN + objective.getDisplayName() + ChatColor.GRAY + " (Story key: " + objective.getStoryModeKey() + ")")
                    .withLore(Arrays.asList(ChatColor.GRAY + "Quest: " + objective.getQuest().getDisplayName(),
                            ChatColor.GRAY + "Completion amount: " + objective.getCompletionAmount()))
                    .build();
            guiItems.add(new GuiItem(itemStack, event -> {
                method.setAccessible(true);
                try {
                    method.invoke(objective, data);
                    player.sendMessage(ChatColor.GREEN + "Set data.");
                    player.closeInventory();
                    quest.save(plugin);
                    open();
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }));
        }
        pane.populateWithGuiItems(guiItems);
        gui.addPane(pane);
    }

    @Override
    public void open() {
        gui.show(getPlayer());
    }
}
