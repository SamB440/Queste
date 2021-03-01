package com.convallyria.queste.gui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Designates a {@link java.lang.reflect.Field} as an editable value in the quest editor GUI.
 * Only primitive values are supported at this time.
 * @see com.convallyria.queste.gui.element.IGuiFieldElement
 * @see com.convallyria.queste.gui.element.IGuiFieldElementRegistry
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface GuiEditable {
    String value();

    GuiEditableType type() default GuiEditableType.DEFAULT;

    enum GuiEditableType {
        CHAT,
        ANVIL,
        DEFAULT
    }
}
