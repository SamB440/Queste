package com.convallyria.queste.api.event;

import com.convallyria.queste.quest.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class QuestEvent extends Event {

    private final Player player;
    private final Quest quest;

    public QuestEvent(final Player player, final Quest quest) {
        this.player = player;
        this.quest = quest;
    }

    /**
     * Gets the player associated with this event.
     * @return the player associated with this event
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the quest associated with this event.
     * @return the quest associated with this event
     */
    public Quest getQuest() {
        return quest;
    }
}
