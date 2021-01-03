package com.convallyria.queste.quest.objective;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;

public final class FishQuestObjective extends QuestObjective {

    public FishQuestObjective(Queste plugin, Quest quest) {
        super(plugin, QuestObjectiveEnum.FISH, quest);
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (this.hasCompleted(player)) return;
        this.increment(player);
    }
}
