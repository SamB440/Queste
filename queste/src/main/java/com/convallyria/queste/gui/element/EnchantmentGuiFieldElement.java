package com.convallyria.queste.gui.element;

import com.convallyria.queste.Queste;
import com.convallyria.queste.api.QuesteAPI;
import com.convallyria.queste.gui.DefaultGUI;
import com.convallyria.queste.gui.IGuiEditable;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EnchantmentGuiFieldElement implements IGuiFieldElement<Enchantment> {

    @Override
    public CompletableFuture<Enchantment> set(Player player, IGuiEditable guiEditable, Field field, Object value) {
        CompletableFuture<Enchantment> completableFuture = new CompletableFuture<>();
        DefaultGUI gui = new DefaultGUI((Queste) QuesteAPI.getAPI(), player);
        gui.open();
        List<GuiItem> guiItems = new ArrayList<>();
        for (Enchantment enchantment : Enchantment.values()) {
            Material representative = Material.BARRIER;
            // Find the first material that represents an enchantment
            for (Material material : Material.values()) {
                if (enchantment.getItemTarget().includes(material)) {
                    representative = material;
                    break;
                }
            }

            ItemStack item = new ItemStack(representative);
            guiItems.add(new GuiItem(item, event -> {
                event.setCancelled(true);
                try {
                    if (field.get(guiEditable) instanceof List) {
                        List<Enchantment> enchantments = (List<Enchantment>) field.get(guiEditable);
                        enchantments.add(enchantment);
                        FieldUtils.writeField(field, guiEditable, enchantments);
                    } else FieldUtils.writeField(field, guiEditable, enchantment);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                completableFuture.complete(enchantment);
            }));
        }
        gui.getPane().populateWithGuiItems(guiItems);
        return completableFuture;
    }

    @Override
    public List<Class<?>> getType() {
        return Arrays.asList(Enchantment.class);
    }

    @Override
    public boolean needsValue() {
        return true;
    }
}
