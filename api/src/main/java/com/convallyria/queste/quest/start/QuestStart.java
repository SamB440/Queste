package com.convallyria.queste.quest.start;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.api.QuesteAPI;
import com.convallyria.queste.gui.IGuiEditable;
import com.convallyria.queste.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class QuestStart implements Listener, IGuiEditable {

    private final String questName;

    protected QuestStart(IQuesteAPI plugin, Quest quest) {
        Bukkit.getPluginManager().registerEvents(this, (Plugin) plugin);
        this.questName = quest.getName();
    }

    public Quest getQuest() {
        return QuesteAPI.getAPI().getManagers().getQuesteCache().getQuest(questName);
    }
}
