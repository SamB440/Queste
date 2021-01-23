package com.convallyria.queste.quest.objective;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;

public final class EnchantQuestObjective extends QuestObjective {

    public EnchantQuestObjective(Queste plugin, Quest quest) {
        super(plugin, quest);
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        if (this.hasCompleted(player)) return;
        this.increment(player);
    }

    @Override
    public String getName() {
        return "Enchant Item";
    }
}
