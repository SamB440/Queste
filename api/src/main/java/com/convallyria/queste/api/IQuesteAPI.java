package com.convallyria.queste.api;

import com.convallyria.queste.managers.IQuesteManagers;
import com.google.gson.Gson;
import net.islandearth.languagy.api.language.Translator;
import org.bukkit.configuration.Configuration;

import java.io.File;
import java.util.logging.Logger;

public interface IQuesteAPI {

    Translator getTranslator();

    Logger getLogger();

    File getDataFolder();

    void saveResource(String path, boolean replace);

    Gson getGson();

    Configuration getConfig();

    IQuesteManagers getManagers();

    boolean debug();
}
