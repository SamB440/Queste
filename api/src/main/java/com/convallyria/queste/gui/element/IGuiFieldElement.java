package com.convallyria.queste.gui.element;

import com.convallyria.queste.gui.IGuiEditable;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IGuiFieldElement<T> {

    CompletableFuture<T> set(Player player, IGuiEditable guiEditable, Field field, Object value);

    List<Class<?>> getType();

    boolean needsValue();
}
