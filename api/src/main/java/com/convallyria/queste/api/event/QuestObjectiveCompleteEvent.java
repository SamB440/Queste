package com.convallyria.queste.api.event;

import com.convallyria.queste.quest.objective.QuestObjective;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class QuestObjectiveCompleteEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final QuestObjective objective;
    private final Player player;

    public QuestObjectiveCompleteEvent(final QuestObjective objective, final Player player) {
        this.objective = objective;
        this.player = player;
    }

    public QuestObjective getObjective() {
        return objective;
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
