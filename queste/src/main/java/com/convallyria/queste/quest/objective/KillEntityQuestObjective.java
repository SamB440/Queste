package com.convallyria.queste.quest.objective;

import com.convallyria.queste.Queste;
import com.convallyria.queste.gui.GuiEditable;
import com.convallyria.queste.quest.Quest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

public final class KillEntityQuestObjective extends QuestObjective {

    @GuiEditable(value = "Entity type")
    private EntityType type;

    public KillEntityQuestObjective(Queste plugin, Quest quest) {
        super(plugin, quest);
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player player = entity.getKiller();
        if (player != null) {
            if (type != null && entity.getType() != type) return;
            if (this.hasCompleted(player)) return;
            this.increment(player);
        }
    }

    @Override
    public String getName() {
        return "Kill Entity";
    }
}
