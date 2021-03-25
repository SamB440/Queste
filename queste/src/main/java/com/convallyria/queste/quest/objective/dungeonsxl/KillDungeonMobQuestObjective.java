package com.convallyria.queste.quest.objective.dungeonsxl;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.quest.objective.QuestObjective;
import de.erethon.dungeonsxl.api.event.mob.DungeonMobDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public final class KillDungeonMobQuestObjective extends QuestObjective {

    public KillDungeonMobQuestObjective(IQuesteAPI api, Quest quest) {
        super(api, quest);
    }

    @EventHandler
    public void onKill(DungeonMobDeathEvent event) {
        Player player = event.getKiller();
        if (player != null) {
            if (hasCompleted(player)) return;
            this.increment(player);
        }
    }

    @Override
    public String getName() {
        return "Kill Dungeon Mob";
    }

    @Override
    public String getPluginRequirement() {
        return "DungeonsXL";
    }
}
