package com.convallyria.queste.quest.objective;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

public final class KillEntityQuestObjective extends QuestObjective {

    public KillEntityQuestObjective(Queste plugin, Quest quest) {
        super(plugin, QuestObjectiveEnum.KILL_ENTITY, quest);
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player player = entity.getKiller();
        if (player != null) {
            if (this.hasCompleted(player)) return;
            this.increment(player);
        }
    }
}
