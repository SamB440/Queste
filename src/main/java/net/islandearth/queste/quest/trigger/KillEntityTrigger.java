package net.islandearth.queste.quest.trigger;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class KillEntityTrigger extends QuestTrigger implements Listener {

    public KillEntityTrigger() {
        super(1);
    }

    @Override
    public String getName() {
        return "Kill Entity";
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player player = entity.getKiller();
        //TODO
    }
}
