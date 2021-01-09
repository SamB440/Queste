package com.convallyria.queste.quest.objective;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import org.bukkit.Effect;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;

public final class FishQuestObjective extends QuestObjective {

    public FishQuestObjective(Queste plugin, Quest quest) {
        super(plugin, quest);
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (this.hasCompleted(player)) return;
        if (event.getCaught() != null && event.getCaught() instanceof Item) {
            this.increment(player).thenAccept(incremented -> {
                if (incremented) {
                    FishHook hook = event.getHook();
                    hook.getWorld().playEffect(hook.getLocation(), Effect.VILLAGER_PLANT_GROW, 0);
                }
            });
        }
    }

    @Override
    public String getName() {
        return "Fish";
    }
}
