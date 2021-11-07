package com.convallyria.queste.gui.element;

import com.convallyria.queste.api.QuesteAPI;
import com.convallyria.queste.gui.IGuiEditable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ListGuiFieldElement implements IGuiFieldElement {

    @Override
    public CompletableFuture<?> set(Player player, IGuiEditable guiEditable, Field field, Object value) {
        CompletableFuture<?> completableFuture = CompletableFuture.completedFuture(null);
        Class<?> type = getType(field);
        if (type != null) {
            IGuiFieldElement<?> element = QuesteAPI.getAPI().getManagers().getGuiFieldElementRegistry().fromClass(type);
            return element.set(player, guiEditable, field, value);
        }
        return completableFuture;
    }

    @Override
    public List<Class<?>> getType() {
        return Arrays.asList(List.class);
    }

    @Nullable
    private Class<?> getType(Field field) {
        Type genericFieldType = field.getGenericType();
        if (genericFieldType instanceof ParameterizedType aType) {
            Type[] fieldArgTypes = aType.getActualTypeArguments();
            for (Type fieldArgType : fieldArgTypes) {
                return (Class<?>) fieldArgType;
            }
        }
        return null;
    }

    @Override
    public boolean needsValue() {
        return true;
    }
}
