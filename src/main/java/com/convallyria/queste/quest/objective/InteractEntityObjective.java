package com.convallyria.queste.quest.objective;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.jetbrains.annotations.NotNull;

public final class InteractEntityObjective extends QuestObjective {

    private EntityType entityType;

    public InteractEntityObjective(Queste plugin, Quest quest) {
        super(plugin, QuestObjectiveEnum.INTERACT_ENTITY, quest);
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if (this.hasCompleted(player)) return;
        boolean flag = entityType == null || entityType == event.getRightClicked().getType();
        if (flag) {
            this.increment(player);
        }
    }

    @NotNull
    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }
}
