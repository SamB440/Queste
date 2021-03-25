package com.convallyria.queste.quest.objective;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.gui.GuiEditable;
import com.convallyria.queste.quest.Quest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.jetbrains.annotations.NotNull;

public final class InteractEntityObjective extends QuestObjective {

    @GuiEditable("Entity Type")
    private EntityType entityType;

    public InteractEntityObjective(IQuesteAPI api, Quest quest) {
        super(api, quest);
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

    @Override
    public String getName() {
        return "Interact With Entity";
    }
}
