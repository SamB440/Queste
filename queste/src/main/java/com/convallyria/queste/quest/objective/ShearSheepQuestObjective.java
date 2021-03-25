package com.convallyria.queste.quest.objective;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.quest.Quest;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;

public final class ShearSheepQuestObjective extends QuestObjective {

    public ShearSheepQuestObjective(IQuesteAPI api, Quest quest) {
        super(api, quest);
    }

    @EventHandler(ignoreCancelled = true)
    public void onShear(PlayerShearEntityEvent event) {
        Entity rightClicked = event.getEntity();
        if (rightClicked instanceof Sheep) {
            Player player = event.getPlayer();
            if (this.hasCompleted(player)) return;
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            ItemStack offHand = player.getInventory().getItemInOffHand();
            if (mainHand.getType() != Material.SHEARS && offHand.getType() != Material.SHEARS) return;
            this.increment(player);
        }
    }

    @Override
    public String getName() {
        return "Shear Sheep";
    }
}
