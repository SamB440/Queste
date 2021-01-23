package com.convallyria.queste.managers;

import com.convallyria.queste.managers.data.IQuesteCache;
import com.convallyria.queste.managers.data.IStorageManager;

public interface IQuesteManagers {

    IStorageManager getStorageManager();

    IQuesteCache getQuesteCache();
}
