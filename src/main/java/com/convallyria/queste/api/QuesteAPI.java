package com.convallyria.queste.api;

import com.convallyria.queste.managers.QuesteManagers;
import net.islandearth.languagy.api.language.Translator;

public interface QuesteAPI {

    QuesteManagers getManagers();

    Translator getTranslator();
}
