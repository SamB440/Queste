package com.convallyria.queste.quest.objective;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.quest.Quest;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

public final class JumpQuestObjective extends QuestObjective {

    public JumpQuestObjective(IQuesteAPI api, Quest quest) {
        super(api, quest);
    }

    @EventHandler
    public void onJump(PlayerStatisticIncrementEvent event) {
        Player player = event.getPlayer();
        if (event.getStatistic() == Statistic.JUMP) {
            if (hasCompleted(player)) return;
            this.increment(player);
        }
    }

    @Override
    public String getName() {
        return "Jump";
    }
}
