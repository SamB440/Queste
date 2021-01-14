package com.convallyria.queste.quest.reward;

import com.convallyria.queste.Queste;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class MoneyReward extends QuestReward {

    private final int amount;

    public MoneyReward(Queste plugin) {
        super(plugin);
        this.amount = 1;
    }

    public MoneyReward(Queste plugin, int amount) {
        super(plugin);
        this.amount = amount;
    }

    @Override
    public void award(Player player) {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }

        RegisteredServiceProvider<Economy> economy = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economy == null) {
            return;
        }

        economy.getProvider().depositPlayer(player, amount);
    }

    @Override
    public String getName() {
        return "Money";
    }
}
