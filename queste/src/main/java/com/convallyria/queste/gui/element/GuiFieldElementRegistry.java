package com.convallyria.queste.gui.element;

import java.util.ArrayList;
import java.util.List;

public class GuiFieldElementRegistry implements IGuiFieldElementRegistry {

    private final List<IGuiFieldElement<?>> guiFieldElements;

    public GuiFieldElementRegistry() {
        this.guiFieldElements = new ArrayList<>();
    }

    public void register(IGuiFieldElement<?> guiFieldElement) {
        guiFieldElements.add(guiFieldElement);
    }

    public void unregister(IGuiFieldElement<?> guiFieldElement) {
        guiFieldElements.remove(guiFieldElement);
    }

    public IGuiFieldElement<?> fromClass(Class<?> clazz) {
        for (IGuiFieldElement<?> type : guiFieldElements) {
            for (Class<?> aClass : type.getType()) {
                if (aClass.isAssignableFrom(clazz)) {
                    return type;
                }
            }
        }
        return new DefaultGuiFieldElement();
    }
}
