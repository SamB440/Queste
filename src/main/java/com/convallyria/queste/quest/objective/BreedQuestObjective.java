package com.convallyria.queste.quest.objective;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityBreedEvent;

public final class BreedQuestObjective extends QuestObjective {

    public BreedQuestObjective(Queste plugin, Quest quest) {
        super(plugin, QuestObjectiveEnum.BREED, quest);
    }

    @EventHandler
    public void onBreed(EntityBreedEvent event) {
        LivingEntity breeder = event.getBreeder();
        if (breeder instanceof Player) {
            Player player = (Player) breeder;
            if (this.hasCompleted(player)) return;
            this.increment(player);
        }
    }
}