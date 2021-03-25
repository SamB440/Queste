package com.convallyria.queste.quest.objective.rpgregions;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.quest.objective.RegionObjective;
import net.islandearth.rpgregions.api.events.RegionDiscoverEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public final class DiscoverRegionQuestObjective extends RegionObjective {

    public DiscoverRegionQuestObjective(IQuesteAPI api, Quest quest) {
        super(api, quest);
    }

    @EventHandler
    public void onDiscover(RegionDiscoverEvent event) {
        Player player = event.getPlayer();
        if (this.hasCompleted(player)) return;
        if (getRegion() != null && !event.getRegion().equals(getRegion())) return;
        this.increment(player);
    }

    @Override
    public String getName() {
        return "Discover Region";
    }

    @Override
    public String getPluginRequirement() {
        return "RPGRegions";
    }
}
