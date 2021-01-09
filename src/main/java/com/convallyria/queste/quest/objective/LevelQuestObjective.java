package com.convallyria.queste.quest.objective;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLevelChangeEvent;

public final class LevelQuestObjective extends QuestObjective {

    private int level;

    public LevelQuestObjective(Queste plugin, Quest quest) {
        super(plugin, quest);
        this.level = 1;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @EventHandler
    public void onLevelChange(PlayerLevelChangeEvent event) {
        Player player = event.getPlayer();
        if (this.hasCompleted(player)) return;
        if (event.getNewLevel() >= level) {
            this.increment(player);
        }
    }

    @Override
    public String getName() {
        return "Level Up";
    }
}
