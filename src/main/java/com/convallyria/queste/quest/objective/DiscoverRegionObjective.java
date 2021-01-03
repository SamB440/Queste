package com.convallyria.queste.quest.objective;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import net.islandearth.rpgregions.api.events.RegionDiscoverEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public final class DiscoverRegionObjective extends RegionObjective {

    public DiscoverRegionObjective(Queste plugin, Quest quest) {
        super(plugin, QuestObjectiveEnum.DISCOVER_REGION, quest);
    }

    @EventHandler
    public void onDiscover(RegionDiscoverEvent event) {
        Player player = event.getPlayer();
        if (this.hasCompleted(player)) return;
        getPlugin().getManagers().getStorageManager().getAccount(player.getUniqueId()).thenAccept(account -> {
            account.getActiveQuests().forEach(quest -> {
                if (quest.getName().equals(this.getQuestName())) {
                    if (getRegion() != null && !event.getRegion().equals(getRegion())) {
                        return;
                    }
                    this.increment(player);
                }
            });
        });
    }
}
