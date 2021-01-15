package com.convallyria.queste.quest.event;

import com.convallyria.queste.quest.Quest;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class QuestLoadEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Quest quest;

    public QuestLoadEvent(Quest quest) {
        this.quest = quest;
    }

    public Quest getQuest() {
        return quest;
    }

    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
