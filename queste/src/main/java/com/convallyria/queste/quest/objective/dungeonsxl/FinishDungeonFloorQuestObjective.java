package com.convallyria.queste.quest.objective.dungeonsxl;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.quest.objective.QuestObjective;
import de.erethon.dungeonsxl.api.event.group.GroupFinishFloorEvent;
import de.erethon.dungeonsxl.api.player.PlayerGroup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public final class FinishDungeonFloorQuestObjective extends QuestObjective {

    public FinishDungeonFloorQuestObjective(IQuesteAPI api, Quest quest) {
        super(api, quest);
    }

    @EventHandler
    public void onFinish(GroupFinishFloorEvent event) {
        PlayerGroup group = event.getGroup();
        group.getInvitedPlayers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                if (hasCompleted(player)) return;
                this.increment(player);
            }
        });
    }

    @Override
    public String getName() {
        return "Finish Dungeon Floor";
    }

    @Override
    public String getPluginRequirement() {
        return "DungeonsXL";
    }
}
