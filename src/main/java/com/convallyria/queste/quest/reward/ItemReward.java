package com.convallyria.queste.quest.reward;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ItemReward extends QuestReward {
	
	private final ItemStack item;
	
	public ItemReward(ItemStack item) {
		this.item = item;
	}
	
	@Override
	public void award(Player player) {
		player.getInventory().addItem(item).forEach((pos, item) -> {
			player.getLocation().getWorld().dropItemNaturally(player.getLocation(), item);
		});
	}

	@Override
	public String getName() {
		return "Item";
	}
}
