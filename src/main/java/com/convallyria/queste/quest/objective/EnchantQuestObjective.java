package com.convallyria.queste.quest.objective;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;

public final class EnchantQuestObjective extends QuestObjective {

    public EnchantQuestObjective(Queste plugin, Quest quest) {
        super(plugin, QuestObjectiveEnum.ENCHANT, quest);
    }

    @EventHandler
    public void onFish(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        if (this.hasCompleted(player)) return;
        this.increment(player);
    }
}
