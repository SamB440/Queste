package com.convallyria.queste.managers.registry;

import com.convallyria.queste.gui.IGuiEditable;

public interface RegistryAccepting {

    void add(IGuiEditable element);

    void remove(IGuiEditable element);
}
