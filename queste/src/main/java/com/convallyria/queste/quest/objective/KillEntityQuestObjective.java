package com.convallyria.queste.quest.objective;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.gui.GuiEditable;
import com.convallyria.queste.quest.Quest;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

public final class KillEntityQuestObjective extends QuestObjective {

    @GuiEditable(value = "Entity type", icon = Material.WOODEN_SWORD)
    private EntityType type;

    @GuiEditable(value = "Entity name", icon = Material.WRITABLE_BOOK)
    private String entityName;

    @GuiEditable(value = "Dropped EXP", icon = Material.EXPERIENCE_BOTTLE)
    private int droppedExp;

    public KillEntityQuestObjective(IQuesteAPI api, Quest quest) {
        super(api, quest);
        this.type = EntityType.ZOMBIE;
        this.entityName = "";
        this.droppedExp = -1;
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player player = entity.getKiller();
        if (player != null) {
            if (type != null && entity.getType() != type) return;
            if (this.hasCompleted(player)) return;
            if (!entityName.isEmpty() && entity.getName().equals(ChatColor.translateAlternateColorCodes('&', entityName))) return;
            if (droppedExp != -1) event.setDroppedExp(droppedExp);
            this.increment(player);
        }
    }

    @Override
    public String getName() {
        return "Kill Entity";
    }
}
