package com.convallyria.queste.quest.requirement;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.gui.GuiEditable;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ItemRequirement extends QuestRequirement {

	@GuiEditable("Item")
	private ItemStack item;

	public ItemRequirement(IQuesteAPI api) {
		super(api);
		this.item = new ItemStack(Material.IRON_AXE);
	}

	@Override
	public boolean meetsRequirements(Player player) {
		for (ItemStack itemStack : player.getInventory()) {
			if (itemStack != null && itemStack.isSimilar(item)) return true;
		}
		return false;
	}

	public ItemStack getItem() {
		return item;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	@Override
	public String getName() {
		return "Item";
	}
}
