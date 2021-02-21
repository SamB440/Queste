package com.convallyria.queste.gui;

import com.convallyria.queste.Queste;
import com.convallyria.queste.colour.ColourScheme;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.quest.objective.QuestObjective;
import com.convallyria.queste.utils.ItemStackBuilder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class QuestViewGUI extends QuesteGUI {

    private final Queste plugin;
    private final Player player;
    private ChestGui gui;

    public QuestViewGUI(Queste plugin, Player player) {
        super(plugin, player);
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void render() {
        this.gui = new ChestGui(6, "Quests");

        PaginatedPane pane = super.generateDefaultConfig();
        List<GuiItem> guiItems = new ArrayList<>();
        plugin.getManagers().getStorageManager().getAccount(player.getUniqueId()).thenAccept(account -> {
            for (Quest activeQuest : account.getActiveQuests()) {
                List<String> lore = new ArrayList<>();
                lore.add(ColourScheme.getPrimaryColour() + "Time: " + ColourScheme.getSecondaryColour() + activeQuest.getTime() + "s");
                lore.add(ColourScheme.getPrimaryColour() + "Objectives: ");
                for (QuestObjective objective : activeQuest.getObjectives()) {
                    lore.add(ColourScheme.getSecondaryColour() + " - " + objective.getDisplayName() + " (x" + objective.getCompletionAmount() + ")");
                }
                ItemStack itemStack = new ItemStackBuilder(Material.TOTEM_OF_UNDYING)
                        .withName(ChatColor.GREEN + activeQuest.getDisplayName())
                        .withLore(lore)
                        .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                        .build();
                guiItems.add(new GuiItem(itemStack, event -> {
                    event.setCancelled(true);
                }));
            }
        }).exceptionally(err -> {
            err.printStackTrace();
            return null;
        });
        pane.populateWithGuiItems(guiItems);
        gui.update();
    }

    @Override
    public ChestGui getGui() {
        return gui;
    }
}
