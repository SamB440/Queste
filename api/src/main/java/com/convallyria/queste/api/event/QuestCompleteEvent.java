package com.convallyria.queste.api.event;

import com.convallyria.queste.quest.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class QuestCompleteEvent extends QuestEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public QuestCompleteEvent(final Quest quest, final Player player) {
        super(player, quest);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
