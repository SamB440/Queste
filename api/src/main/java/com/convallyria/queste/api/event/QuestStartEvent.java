package com.convallyria.queste.api.event;

import com.convallyria.queste.quest.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class QuestStartEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Quest quest;
    private final Player player;

    public QuestStartEvent(final Quest quest, final Player player) {
        this.quest = quest;
        this.player = player;
    }

    public Quest getQuest() {
        return quest;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
