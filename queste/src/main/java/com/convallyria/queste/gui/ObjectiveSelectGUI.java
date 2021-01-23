package com.convallyria.queste.gui;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.quest.objective.QuestObjective;
import com.convallyria.queste.utils.ItemStackBuilder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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

        PaginatedPane pane = super.render();
        List<QuestObjective> objectives = quest.getObjectivesFromType(type);
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
        gui.update();
    }

    @Override
    public ChestGui getGui() {
        return gui;
    }

    @Override
    public void open() {
        gui.show(getPlayer());
    }
}
