package com.convallyria.queste.quest.objective;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.gui.GuiEditable;
import com.convallyria.queste.quest.Quest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;

import java.util.ArrayList;
import java.util.List;

public final class EnchantQuestObjective extends QuestObjective {

    @GuiEditable("Valid enchantments")
    private List<Enchantment> enchantments;

    public EnchantQuestObjective(IQuesteAPI api, Quest quest) {
        super(api, quest);
        this.enchantments = new ArrayList<>();
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        if (enchantments == null) enchantments = new ArrayList<>(); // temp
        if (this.hasCompleted(player)) return;
        boolean flag = enchantments.isEmpty() || event.getEnchantsToAdd().keySet().stream().anyMatch(enchantments::contains);
        if (flag) this.increment(player);
    }

    @Override
    public String getName() {
        return "Enchant Item";
    }
}
