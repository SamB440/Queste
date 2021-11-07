package com.convallyria.queste.gui;

import com.convallyria.queste.Queste;
import com.convallyria.queste.chat.QuesteConversationPrefix;
import com.convallyria.queste.chat.QuesteStringPrompt;
import com.convallyria.queste.managers.registry.QuesteRegistry;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.utils.ItemStackBuilder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class QuestCreateGUI extends QuesteGUI {

    private final Queste plugin;
    private final Player player;
    private final Quest quest;
    private ChestGui gui;

    public QuestCreateGUI(Queste plugin, Player player, Quest quest) {
        super(plugin, player);
        this.plugin = plugin;
        this.player = player;
        this.quest = quest;
    }

    @Override
    public void render() {
        this.gui = new ChestGui(6, quest.getName());
        gui.setOnGlobalClick(click -> click.setCancelled(true));

        OutlinePane oPane = new OutlinePane(0, 1, 9, 1, Pane.Priority.HIGHEST);
        oPane.setRepeat(true);
        oPane.addItem(new GuiItem(new ItemStackBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .withName(" ")
                .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                .build()));
        gui.addPane(oPane);

        // Add all the registry editors
        int x = 2;
        for (QuesteRegistry<?> registry : plugin.getManagers().getRegistries()) {
            StaticPane registryPane = new StaticPane(x, 2, 1, 1, Pane.Priority.HIGH);
            ItemStackBuilder registryItem = new ItemStackBuilder(registry.getIcon())
                    .withName("&6" + registry.getRegistryName());
            for (String description : registry.getDescription()) {
                registryItem.withLore(description); // Use this to translate colours
            }

            registryPane.addItem(new GuiItem(registryItem.build(), event -> {
                player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1f, 1f);
                new QueryGUI(plugin, player, quest, registry).open();
            }),0, 0);
            gui.addPane(registryPane);
            x = x + 2;
        }

        StaticPane questInfo = new StaticPane(4, 0, 1, 1, Pane.Priority.HIGH);
        ItemStack questInfoItem = new ItemStackBuilder(Material.TOTEM_OF_UNDYING)
                .withName("&6" + quest.getName())
                .withLore("&7" + quest.getDescription(), "&e&lClick &7to save the quest.")
                .build();
        questInfo.addItem(new GuiItem(questInfoItem, event -> {
            quest.save(plugin);
            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1f);
        }),0, 0);
        gui.addPane(questInfo);

        StaticPane displayName = new StaticPane(0, 2, 1, 1, Pane.Priority.HIGH);
        ItemStack displayNameItem = new ItemStackBuilder(Material.PAPER)
                .withName("&6Set quest display name &7- &6" + quest.getDisplayName())
                .withLore("&7Your quest can be renamed at any time.", "&e&lClick &7to rename the quest.")
                .build();
        displayName.addItem(new GuiItem(displayNameItem, event -> {
            ConversationFactory factory = new ConversationFactory(plugin)
                    .withModality(true)
                    .withPrefix(new QuesteConversationPrefix())
                    .withFirstPrompt(new QuesteStringPrompt("What display name should this quest have?"))
                    .withEscapeSequence("quit")
                    .withLocalEcho(true)
                    .withTimeout(60);
            Conversation conversation = factory.buildConversation(player);
            conversation.begin();
            conversation.addConversationAbandonedListener(abandonedEvent -> {
                quest.setDisplayName((String) abandonedEvent.getContext().getSessionData("input"));
                open();
            });
            player.closeInventory();
        }),0, 0);
        gui.addPane(displayName);

        StaticPane description = new StaticPane(0, 3, 1, 1, Pane.Priority.HIGH);
        ItemStack descriptionItem = new ItemStackBuilder(Material.PAPER)
                .withName("&6Set quest description &7- &6" + quest.getDescription())
                .withLore("&7What description the quest should have.", "&e&lClick &7to set the quest description.")
                .build();
        description.addItem(new GuiItem(descriptionItem, event -> {
            ConversationFactory factory = new ConversationFactory(plugin)
                    .withModality(true)
                    .withPrefix(new QuesteConversationPrefix())
                    .withFirstPrompt(new QuesteStringPrompt("What description should this quest have?"))
                    .withEscapeSequence("quit")
                    .withLocalEcho(true)
                    .withTimeout(60);
            Conversation conversation = factory.buildConversation(player);
            conversation.begin();
            conversation.addConversationAbandonedListener(abandonedEvent -> {
                quest.setDescription((String) abandonedEvent.getContext().getSessionData("input"));
                open();
            });
            player.closeInventory();
        }),0, 0);
        gui.addPane(description);

        StaticPane delete = new StaticPane(0, 3, 1, 1, Pane.Priority.HIGH);
        ItemStack deleteItem = new ItemStackBuilder(Material.REDSTONE)
                .withName("&c&lDelete Quest")
                .withLore(" ", "&cThis action cannot be undone.", " ", "&c&lShift-Click &7to delete the quest.")
                .build();
        delete.addItem(new GuiItem(deleteItem, event -> {
            if (event.getClick() == ClickType.SHIFT_LEFT) {
                //TODO chat delete
            }
            player.closeInventory();
        }),0, 0);
        gui.addPane(delete);

        StaticPane story = new StaticPane(6, 3, 1, 1, Pane.Priority.HIGH);
        ItemStack storyItem = new ItemStackBuilder(Material.ORANGE_BANNER)
                .withName("&6Toggle story quest &7- &6" + quest.isStoryMode())
                .withLore("&7A story quest requires objectives", "&7to be completed in order.", "&c&lShift-Click &7to toggle story quest.")
                .build();
        story.addItem(new GuiItem(storyItem, event -> {
            if (event.getClick() == ClickType.SHIFT_LEFT) {
                quest.setStoryMode(!quest.isStoryMode());
                player.playSound(player.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1f, 1f);
                open();
            }
        }),0, 0);
        gui.addPane(story);

        StaticPane restartable = new StaticPane(6, 4, 1, 1, Pane.Priority.HIGH);
        ItemStack restartableItem = new ItemStackBuilder(Material.GREEN_BANNER)
                .withName("&6Toggle restartable &7- &6" + quest.canRestart())
                .withLore("&7Whether this quest can be started multiple times", "&c&lShift-Click &7to toggle restartable.")
                .build();
        restartable.addItem(new GuiItem(restartableItem, event -> {
            if (event.getClick() == ClickType.SHIFT_LEFT) {
                quest.setCanRestart(!quest.canRestart());
                player.playSound(player.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1f, 1f);
                open();
            }
        }),0, 0);
        gui.addPane(restartable);

        StaticPane dummy = new StaticPane(6, 5, 1, 1, Pane.Priority.HIGH);
        ItemStack dummyItem = new ItemStackBuilder(Material.RED_BANNER)
                .withName("&6Toggle dummy quest &7- &6" + quest.isDummy())
                .withLore("&7Whether this quest is simply for editing", "&7Quest &ccannot &7be used by players", "&c&lShift-Click &7to toggle dummy.")
                .build();
        dummy.addItem(new GuiItem(dummyItem, event -> {
            if (event.getClick() == ClickType.SHIFT_LEFT) {
                quest.setDummy(!quest.isDummy());
                player.playSound(player.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1f, 1f);
                open();
            }
        }),0, 0);
        gui.addPane(dummy);
        gui.update();
    }

    @Override
    public ChestGui getGui() {
        return gui;
    }
}
