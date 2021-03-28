package com.convallyria.queste.gui;

import com.convallyria.queste.Queste;
import com.convallyria.queste.chat.QuesteConversationPrefix;
import com.convallyria.queste.chat.QuesteStringPrompt;
import com.convallyria.queste.gui.element.ICustomGuiFeedback;
import com.convallyria.queste.gui.element.IGuiFieldElement;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.translation.Translations;
import com.convallyria.queste.utils.ItemStackBuilder;
import com.convallyria.queste.utils.ReflectionUtils;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EditGuiElementGUI extends QuesteGUI {

    private final Queste plugin;
    private final Player player;
    private final Quest quest;
    private final IGuiEditable guiEditable;
    private ChestGui gui;

    public EditGuiElementGUI(Queste plugin, Player player, Quest quest, IGuiEditable guiEditable) {
        super(plugin, player);
        this.plugin = plugin;
        this.player = player;
        this.quest = quest;
        this.guiEditable = guiEditable;
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
        gui.setTitle("Loading...");
        CompletableFuture<List<Field>> future = ReflectionUtils.getSuperFieldsFromAnnotationAsync(guiEditable.getClass(), GuiEditable.class);
        future.thenAccept(fields -> {
            for (Field field : fields) {
                GuiEditable annotation = field.getAnnotation(GuiEditable.class);
                String name = annotation.value();
                ItemStack item = new ItemStackBuilder(Material.WRITTEN_BOOK)
                        .withName("&6" + name + "&7 - &6" + getField(field))
                        .withLore("&e&lClick &7to set value.")
                        .generation(null).build();
                GuiItem guiItem = new GuiItem(item, click -> {
                    IGuiFieldElement element = plugin.getManagers().getGuiFieldElementRegistry().fromClass(field.getType());
                    if (element != null) {
                        if (!element.needsValue()) { // Just toggle it.
                            element.set(player, guiEditable, field, null).thenAccept(done -> open());
                        } else {
                            if (annotation.type() == GuiEditable.GuiEditableType.CHAT) {
                                String info = getInfoIfApplicable(field.getName());
                                if (info != null) player.sendMessage(ChatColor.GREEN + "Info provided: " + ChatColor.GRAY + info);
                                ConversationFactory factory = new ConversationFactory(plugin)
                                        .withModality(true)
                                        .withPrefix(new QuesteConversationPrefix())
                                        .withFirstPrompt(new QuesteStringPrompt("Enter value:"))
                                        .withEscapeSequence("quit")
                                        .withLocalEcho(true)
                                        .withTimeout(60);
                                Conversation conversation = factory.buildConversation(player);
                                conversation.begin();
                                conversation.addConversationAbandonedListener(abandonedEvent -> {
                                    String input = (String) abandonedEvent.getContext().getSessionData("input");
                                    boolean flag = false;
                                    if (ICustomGuiFeedback.class.isAssignableFrom(guiEditable.getClass())) {
                                        ICustomGuiFeedback customGuiFeedback = (ICustomGuiFeedback) guiEditable;
                                        flag = customGuiFeedback.feedback(player, input);
                                    }

                                    if (!flag) {
                                        element.set(player, guiEditable, field, input);
                                    }
                                    player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f);
                                    open();
                                });
                                player.closeInventory();
                                return;
                            }

                            new AnvilGUI.Builder()
                                .onClose(player -> {
                                    open();
                                })
                                .onComplete((player, text) -> {
                                    if (ICustomGuiFeedback.class.isAssignableFrom(guiEditable.getClass())) {
                                        ICustomGuiFeedback customGuiFeedback = (ICustomGuiFeedback) guiEditable;
                                        customGuiFeedback.feedback(player, text);
                                        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f);
                                        return AnvilGUI.Response.close();
                                    }

                                    element.set(player, guiEditable, field, text).thenAccept(done -> {
                                        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f);
                                        open();
                                    });
                                    return AnvilGUI.Response.text("Please wait.");
                                })
                                .text("Enter value")
                                .itemLeft(new ItemStack(Material.WRITABLE_BOOK))
                                .title("Set value of this element")
                                .plugin(plugin)
                                .open(player);
                        }
                    }
                    player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f);
                });
                items.add(guiItem);
            }

            Bukkit.getScheduler().runTask(plugin, () -> { // GUI updates must be sync.
                pane.populateWithGuiItems(items);
                gui.setTitle(quest.getName());
                gui.update();
            });
        });
        pane.populateWithGuiItems(items);
        gui.update();
    }

    private String getInfoIfApplicable(String field) {
        if (ICustomGuiFeedback.class.isAssignableFrom(guiEditable.getClass())) {
            ICustomGuiFeedback customGuiFeedback = (ICustomGuiFeedback) guiEditable;
            return customGuiFeedback.info(field);
        }
        return null;
    }

    private Object getField(Field field) { // Don't throw exceptions for cleaner code elsewhere
        try {
            field.setAccessible(true);
            return field.get(guiEditable);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ChestGui getGui() {
        return gui;
    }
}
