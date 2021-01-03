package com.convallyria.queste.quest.objective;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

public final class PlaceBlockQuestObjective extends QuestObjective {

    public PlaceBlockQuestObjective(Queste plugin, Quest quest) {
        super(plugin, QuestObjectiveEnum.PLACE_BLOCK, quest);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (this.hasCompleted(player)) return;
        getPlugin().getManagers().getStorageManager().getAccount(player.getUniqueId()).thenAccept(account -> {
            account.getActiveQuests().forEach(quest -> {
                if (quest.getName().equals(this.getQuestName())) {
                    this.increment(player);
                }
            });
        });
    }
}
