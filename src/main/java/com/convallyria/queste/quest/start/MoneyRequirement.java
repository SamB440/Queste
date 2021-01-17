package com.convallyria.queste.quest.start;

import com.convallyria.queste.Queste;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class MoneyRequirement extends QuestRequirement {

    private final int amount;

    public MoneyRequirement(Queste plugin) {
        super(plugin);
        this.amount = 1;
    }

    @Override
    public boolean meetsRequirements(Player player) {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> economy = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economy == null) {
            return false;
        }

        return economy.getProvider().getBalance(player) >= amount;
    }

    @Override
    public String getName() {
        return "Money";
    }
}
