package com.convallyria.queste.quest.reward;

import com.convallyria.queste.Queste;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ItemReward extends QuestReward {
	
	private final ItemStack item;

	public ItemReward(Queste plugin) {
		super(plugin);
		this.item = new ItemStack(Material.IRON_AXE);
	}

	public ItemReward(Queste plugin, ItemStack item) {
		super(plugin);
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
