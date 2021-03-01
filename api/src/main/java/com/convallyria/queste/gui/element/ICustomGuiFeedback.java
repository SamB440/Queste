package com.convallyria.queste.gui.element;

import org.bukkit.entity.Player;

public interface ICustomGuiFeedback {

    void feedback(Player player, String input);

    default String info() {
        return null;
    }
}
