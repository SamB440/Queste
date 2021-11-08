package com.convallyria.queste.quest.objective;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.gui.GuiEditable;
import com.convallyria.queste.quest.Quest;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.inventory.ItemStack;

public final class BreedQuestObjective extends QuestObjective {

    @GuiEditable(value = "Breed Item", icon = Material.WHEAT)
    private ItemStack breedItem;

    @GuiEditable(value = "Entity Type", icon = Material.WOODEN_SWORD)
    private EntityType entityType;

    public BreedQuestObjective(IQuesteAPI api, Quest quest) {
        super(api, quest);
        this.breedItem = new ItemStack(Material.WHEAT);
        this.entityType = EntityType.SHEEP;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreed(EntityBreedEvent event) {
        LivingEntity breeder = event.getBreeder();
        if (breeder instanceof Player player) {
            if (this.hasCompleted(player)) return;
            if (event.getBredWith() != null && !event.getBredWith().isSimilar(breedItem)) return;
            if (event.getEntityType() != entityType) return;
            this.increment(player);
        }
    }

    @Override
    public String getName() {
        return "Breed Animals";
    }
}
