package com.convallyria.queste.gui;

import com.convallyria.queste.Queste;
import com.convallyria.queste.managers.registry.IQuesteRegistry;
import com.convallyria.queste.managers.registry.QuesteRegistry;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.quest.objective.QuestObjective;
import com.convallyria.queste.quest.objective.QuestObjectiveRegistry;
import com.convallyria.queste.quest.requirement.QuestRequirement;
import com.convallyria.queste.quest.requirement.QuestRequirementRegistry;
import com.convallyria.queste.quest.reward.QuestReward;
import com.convallyria.queste.quest.reward.QuestRewardRegistry;
import com.convallyria.queste.quest.start.QuestStart;
import com.convallyria.queste.quest.start.QuestStartRegistry;
import com.convallyria.queste.translation.Translations;
import com.convallyria.queste.utils.ItemStackBuilder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class EditQuestElementGUI extends QuesteGUI {

    private final Queste plugin;
    private final Player player;
    private final Quest quest;
    private final QuesteRegistry<?> registry;
    private ChestGui gui;

    public EditQuestElementGUI(Queste plugin, Player player, Quest quest, QuesteRegistry<?> registry) {
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
        StaticPane exit = new StaticPane(exitX, exitY, exitL, exitH, Pane.Priority.HIGHEST);
        // Exit item
        Material em = Material.valueOf(plugin.getConfig().getString("settings.server.gui.exit.exit"));
        ItemStack exitItem = new ItemStackBuilder(em)
                .withName(Translations.EXIT.get(player))
                .withLore(Translations.EXIT_LORE.getList(player))
                .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                .build();
        exit.addItem(new GuiItem(exitItem, event -> {
            new QuestCreateGUI(plugin, player, quest).open();
            player.playSound(player.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1f, 1f);
        }), 0, 0);
        gui.addPane(exit);

        List<GuiItem> items = new ArrayList<>();
        if (registry instanceof QuestObjectiveRegistry) {
            for (QuestObjective objective : quest.getObjectives()) {
                ItemStack item = new ItemStackBuilder(Material.WRITTEN_BOOK)
                        .withName("&6" + objective.getName())
                        .withLore("&6Completion amount: " + objective.getCompletionAmount(),
                                    "&6Display name: " + objective.getDisplayName(),
                                    "&6Story key: " + objective.getStoryModeKey(),
                                    " ",
                                    "&c&lShift-Click &cto remove.")
                        .build();
                GuiItem guiItem = new GuiItem(item, click -> {
                    if (click.getClick() == ClickType.SHIFT_LEFT) {
                        quest.getObjectives().remove(objective);
                        return;
                    }
                    new EditGuiElementGUI(plugin, player, quest, objective).open();
                    player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f);
                });
                items.add(guiItem);
            }
        } else if (registry instanceof QuestRewardRegistry) {
            for (QuestReward reward : quest.getRewards()) {
                items.add(getDefaultGuiItem(reward, registry, clickType -> {
                    if (clickType == ClickType.SHIFT_LEFT) {
                        quest.getRewards().remove(reward);
                        return true;
                    }
                    return false;
                }));
            }
        } else if (registry instanceof QuestRequirementRegistry) {
            for (QuestRequirement requirement : quest.getRequirements()) {
                items.add(getDefaultGuiItem(requirement, registry, clickType -> {
                    if (clickType == ClickType.SHIFT_LEFT) {
                        quest.getRequirements().remove(requirement);
                        return true;
                    }
                    return false;
                }));
            }
        } else if (registry instanceof QuestStartRegistry) {
            for (QuestStart start : quest.getStarters()) {
                items.add(getDefaultGuiItem(start, registry, clickType -> {
                    if (clickType == ClickType.SHIFT_LEFT) {
                        quest.getStarters().remove(start);
                        return true;
                    }
                    return false;
                }));
            }
        }
        pane.populateWithGuiItems(items);
        gui.update();
    }

    public GuiItem getDefaultGuiItem(IGuiEditable guiEditable, IQuesteRegistry<?> registry, Predicate<ClickType> function) {
        ItemStack item = new ItemStackBuilder(registry.getIcon())
                .withName("&6" + guiEditable.getName())
                .withLore("&7No additional data to display", "&c&lShift-Click &cto remove.")
                .build();
        GuiItem guiItem = new GuiItem(item);
        guiItem.setAction(click -> {
            if (function.test(click.getClick())) {
                guiItem.setVisible(false);
                gui.update();
                return;
            }
            new EditGuiElementGUI(plugin, player, quest, guiEditable).open();
            player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f);
        });
        return guiItem;
    }

    @Override
    public ChestGui getGui() {
        return gui;
    }
}
