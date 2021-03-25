package com.convallyria.queste.managers;

import com.convallyria.queste.gui.element.IGuiFieldElementRegistry;
import com.convallyria.queste.managers.data.IQuesteCache;
import com.convallyria.queste.managers.data.IStorageManager;
import com.convallyria.queste.managers.registry.IQuesteRegistry;

public interface IQuesteManagers {

    IStorageManager getStorageManager();

    IQuesteCache getQuesteCache();

    IGuiFieldElementRegistry getGuiFieldElementRegistry();

    IQuesteRegistry<?> getQuestRegistry(Class<? extends IQuesteRegistry<?>> clazz);
}
