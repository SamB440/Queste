package com.convallyria.queste.gui;

import org.bukkit.Material;

/**
 * Represents a class that can be displayed in GUIs and edited.
 */
public interface IGuiEditable {

    /**
     * User friendly name.
     * @return name of this implementation
     */
    String getName();

    /**
     * The material to display in GUIs. Defaults to WRITTEN_BOOK.
     * @return material to display in GUIs
     */
    default Material getIcon() {
        return Material.WRITTEN_BOOK;
    }
}
