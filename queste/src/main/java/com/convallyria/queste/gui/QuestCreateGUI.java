package com.convallyria.queste.gui;

import com.convallyria.queste.Queste;
import com.convallyria.queste.chat.QuesteConversationPrefix;
import com.convallyria.queste.chat.QuesteStringPrompt;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.quest.objective.QuestObjectiveRegistry;
import com.convallyria.queste.quest.requirement.QuestRequirementRegistry;
import com.convallyria.queste.quest.reward.QuestRewardRegistry;
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

import java.util.Arrays;

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

        StaticPane requirements = new StaticPane(4, 2, 1, 1, Pane.Priority.HIGH);
        ItemStack requirementsItem = new ItemStackBuilder(Material.COMPARATOR)
                .withName("&6Requirements")
                .withLore("&7Requirements before a quest can", "&7be started by a player", "&e&lClick &7to edit quest requirements.")
                .build();
        requirements.addItem(new GuiItem(requirementsItem, event -> {
            player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1f, 1f);
            ItemStack editRequirements = new ItemStackBuilder(Material.WRITABLE_BOOK)
                    .withName("&6Edit requirements")
                    .build();
            ItemStack addObjectives = new ItemStackBuilder(Material.WRITABLE_BOOK)
                    .withName("&6Add requirements")
                    .build();
            GuiItem editRequirementGuiItem = new GuiItem(editRequirements, click -> {
                new EditQuestElementGUI(plugin, player, quest, plugin.getManagers().getQuestRegistry(QuestRequirementRegistry.class)).open();
                player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
            });
            GuiItem addRequirementGuiItem = new GuiItem(addObjectives, click -> {
                new AddQuestElementGUI(plugin, player, quest, plugin.getManagers().getQuestRegistry(QuestRequirementRegistry.class)).open();
                player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
            });
            new QueryGUI(plugin, player, Arrays.asList(addRequirementGuiItem, editRequirementGuiItem)).open();
        }),0, 0);
        gui.addPane(requirements);

        StaticPane objectives = new StaticPane(6, 2, 1, 1, Pane.Priority.HIGH);
        ItemStack objectivesItem = new ItemStackBuilder(Material.WRITABLE_BOOK)
                .withName("&6Objectives")
                .withLore("&7Tasks required to complete the quest", "&e&lClick &7to edit quest objectives.")
                .build();
        objectives.addItem(new GuiItem(objectivesItem, event -> {
            player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
            ItemStack editObjectives = new ItemStackBuilder(Material.WRITABLE_BOOK)
                    .withName("&6Edit objectives")
                    .build();
            ItemStack addObjectives = new ItemStackBuilder(Material.WRITABLE_BOOK)
                    .withName("&6Add objectives")
                    .build();
            GuiItem editObjectiveGuiItem = new GuiItem(editObjectives, click -> {
                new EditQuestElementGUI(plugin, player, quest, plugin.getManagers().getQuestRegistry(QuestObjectiveRegistry.class)).open();
                player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
            });
            GuiItem addObjectiveGuiItem = new GuiItem(addObjectives, click -> {
                new AddQuestElementGUI(plugin, player, quest, plugin.getManagers().getQuestRegistry(QuestObjectiveRegistry.class)).open();
                player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
            });
            new QueryGUI(plugin, player, Arrays.asList(addObjectiveGuiItem, editObjectiveGuiItem)).open();
        }),0, 0);
        gui.addPane(objectives);

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

        StaticPane rewards = new StaticPane(8, 2, 1, 1, Pane.Priority.HIGH);
        ItemStack rewardsItem = new ItemStackBuilder(Material.CHEST)
                .withName("&6Rewards")
                .withLore("&7Rewards are granted upon quest completion", "&e&lClick &7to edit quest rewards.")
                .build();
        rewards.addItem(new GuiItem(rewardsItem, event -> {
            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1f, 1f);
            ItemStack editRewards = new ItemStackBuilder(Material.WRITABLE_BOOK)
                    .withName("&6Edit rewards")
                    .build();
            ItemStack addRewards = new ItemStackBuilder(Material.WRITABLE_BOOK)
                    .withName("&6Add rewards")
                    .build();
            GuiItem editRewardGuiItem = new GuiItem(editRewards, click -> {
                new EditQuestElementGUI(plugin, player, quest, plugin.getManagers().getQuestRegistry(QuestRewardRegistry.class)).open();
                player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
            });
            GuiItem addRewardGuiItem = new GuiItem(addRewards, click -> {
                new AddQuestElementGUI(plugin, player, quest, plugin.getManagers().getQuestRegistry(QuestRewardRegistry.class)).open();
                player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1f, 1f);
            });
            new QueryGUI(plugin, player, Arrays.asList(addRewardGuiItem, editRewardGuiItem)).open();
        }),0, 0);
        gui.addPane(rewards);
        gui.update();
    }

    @Override
    public ChestGui getGui() {
        return gui;
    }
}
