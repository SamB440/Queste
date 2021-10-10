package com.convallyria.queste.quest.objective.citizens;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.gui.GuiEditable;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.utils.InventoryUtils;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class CitizenDeliverItemQuestObjective extends CitizensQuestObjective {

    @GuiEditable("Item Type")
    private Material item;
    @GuiEditable("Item Amount")
    private int amount;

    public CitizenDeliverItemQuestObjective(IQuesteAPI api, Quest quest) {
        super(api, quest);
        this.item = Material.SPRUCE_LOG;
        this.amount = 1;
    }

    @EventHandler
    public void onRightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        NPC npc = event.getNPC();
        if (npc.getId() == getNpcId()) {
            if (this.hasCompleted(player)) return;
            if (player.getInventory().containsAtLeast(new ItemStack(item), amount)) {
                this.increment(player);
                InventoryUtils.removeItems(player.getInventory(), item, amount);
            }
        }
    }

    @Override
    public String getName() {
        return "Deliver to NPC";
    }
}
