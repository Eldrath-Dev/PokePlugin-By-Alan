package com.alan.pokePlugin.managers;

import com.alan.pokePlugin.PokePlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyManager {

    private final PokePlugin plugin;
    private Economy economy;
    private boolean economyEnabled;

    public EconomyManager(PokePlugin plugin) {
        this.plugin = plugin;
        this.economyEnabled = false;
    }

    public void setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().warning("Vault not found! Economy features disabled.");
            economyEnabled = false;
            return;
        }

        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            plugin.getLogger().warning("No economy plugin found! Economy features disabled.");
            economyEnabled = false;
            return;
        }

        economy = rsp.getProvider();
        economyEnabled = true;
        plugin.getLogger().info("Vault economy hooked successfully! (" + economy.getName() + ")");
    }

    public boolean isEconomyEnabled() {
        return economyEnabled && economy != null;
    }

    public boolean hasBalance(Player player, double amount) {
        if (!isEconomyEnabled()) {
            return true;
        }
        return economy.has(player, amount);
    }

    public boolean withdraw(Player player, double amount) {
        if (!isEconomyEnabled()) {
            return true;
        }
        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    public double getBalance(Player player) {
        if (!isEconomyEnabled()) {
            return 0;
        }
        return economy.getBalance(player);
    }

    public Economy getEconomy() {
        return economy;
    }
}