package com.convallyria.queste.quest.objective;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.quest.Quest;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityBreedEvent;

public final class BreedQuestObjective extends QuestObjective {

    public BreedQuestObjective(IQuesteAPI api, Quest quest) {
        super(api, quest);
    }

    @EventHandler
    public void onBreed(EntityBreedEvent event) {
        LivingEntity breeder = event.getBreeder();
        if (breeder instanceof Player player) {
            if (this.hasCompleted(player)) return;
            this.increment(player);
        }
    }

    @Override
    public String getName() {
        return "Breed Animals";
    }
}
