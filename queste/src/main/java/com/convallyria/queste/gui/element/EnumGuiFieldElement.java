package com.convallyria.queste.gui.element;

import com.convallyria.queste.chat.preset.ReturnValueConversationPreset;
import com.convallyria.queste.gui.IGuiEditable;
import com.google.common.base.Enums;
import com.google.common.base.Optional;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class EnumGuiFieldElement implements IGuiFieldElement {

    @Override
    public CompletableFuture<Void> set(Player player, IGuiEditable guiEditable, Field field, Object value) {
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();
        try {
            if (!field.get(guiEditable).getClass().isEnum()) {
                completableFuture.complete(null);
                return completableFuture;
            }

            Class<? extends Enum> enumClazz = (Class<? extends Enum>) field.get(guiEditable).getClass();
            System.out.println(Enum.class.isAssignableFrom(enumClazz));
            List<? extends Enum<?>> enumList = EnumUtils.getEnumList(enumClazz);

            new ReturnValueConversationPreset(player, "Enter the value, valid entries are: " + enumList, input -> {
                Optional<?> enumValue = Enums.getIfPresent(enumClazz, input.toUpperCase(Locale.ROOT));
                if (!enumValue.isPresent()) {
                    player.sendMessage(ChatColor.RED + "No enum value by that name was found.");
                    completableFuture.complete(null);
                    return;
                }

                try {
                    field.set(guiEditable, enumValue.get());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                completableFuture.complete(null);
            });
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return completableFuture;
    }

    @Override
    public List<Class<?>> getType() {
        return Arrays.asList(Enum.class);
    }

    @Override
    public boolean needsValue() {
        return false;
    }
}
