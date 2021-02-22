package com.convallyria.queste.quest.reward;

import com.convallyria.queste.gui.GuiEditable;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ItemReward extends QuestReward {

	@GuiEditable("Item")
	private ItemStack item;

	public ItemReward() {
		this.item = new ItemStack(Material.IRON_AXE);
	}

	@Override
	public void award(Player player) {
		player.getInventory().addItem(item).forEach((pos, dropItem) -> {
			player.getLocation().getWorld().dropItemNaturally(player.getLocation(), dropItem);
		});
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
