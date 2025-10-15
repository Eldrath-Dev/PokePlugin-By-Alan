package com.alan.pokePlugin;

import com.alan.pokePlugin.commands.PokeCommand;
import com.alan.pokePlugin.managers.ConfigManager;
import com.alan.pokePlugin.managers.CooldownManager;
import com.alan.pokePlugin.managers.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PokePlugin extends JavaPlugin {

    private static PokePlugin instance;
    private ConfigManager configManager;
    private CooldownManager cooldownManager;
    private EconomyManager economyManager;

    private boolean isFolia = false;
    private boolean isPaper = false;
    private boolean isPurpur = false;
    private boolean isSpigot = false;
    private String serverType = "Unknown";

    @Override
    public void onEnable() {
        instance = this;

        detectServerPlatform();

        configManager = new ConfigManager(this);
        configManager.loadConfig();

        cooldownManager = new CooldownManager(this);
        cooldownManager.loadCooldowns();

        economyManager = new EconomyManager(this);
        economyManager.setupEconomy();

        registerCommands();

        logStartupInfo();
    }

    @Override
    public void onDisable() {
        if (cooldownManager != null) {
            cooldownManager.saveCooldowns();
        }
        getLogger().info("PokePlugin has been disabled!");
    }

    private void detectServerPlatform() {
        // Check for Folia first (most specific)
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            isFolia = true;
            serverType = "Folia";
            getLogger().info("Detected Folia server - using regionized task scheduler");
            return;
        } catch (ClassNotFoundException ignored) {
        }

        // Check for Purpur
        try {
            Class.forName("org.purpurmc.purpur.PurpurConfig");
            isPurpur = true;
            isPaper = true; // Purpur extends Paper
            serverType = "Purpur";
            getLogger().info("Detected Purpur server");
            return;
        } catch (ClassNotFoundException ignored) {
        }

        // Check for Paper
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            isPaper = true;
            serverType = "Paper";
            getLogger().info("Detected Paper server");
            return;
        } catch (ClassNotFoundException ignored) {
        }

        // Check for modern Paper (1.19+)
        try {
            Class.forName("io.papermc.paper.configuration.Configuration");
            isPaper = true;
            serverType = "Paper";
            getLogger().info("Detected Paper server (modern)");
            return;
        } catch (ClassNotFoundException ignored) {
        }

        // Check for Spigot
        try {
            Class.forName("org.spigotmc.SpigotConfig");
            isSpigot = true;
            serverType = "Spigot";
            getLogger().info("Detected Spigot server");
            return;
        } catch (ClassNotFoundException ignored) {
        }

        // Fallback to Bukkit
        serverType = "Bukkit";
        getLogger().info("Detected Bukkit server (or unknown variant)");
    }

    private void registerCommands() {
        PokeCommand pokeCommand = new PokeCommand(this);
        getCommand("poke").setExecutor(pokeCommand);
        getCommand("poke").setTabCompleter(pokeCommand);
    }

    private void logStartupInfo() {
        getLogger().info("╔════════════════════════════════════════╗");
        getLogger().info("║      PokePlugin v" + getDescription().getVersion() + " Enabled      ║");
        getLogger().info("╠════════════════════════════════════════╣");
        getLogger().info("║ Server Type: " + String.format("%-26s", serverType) + "║");
        getLogger().info("║ Server Version: " + String.format("%-23s", Bukkit.getVersion().split("-")[0]) + "║");
        getLogger().info("║ Economy: " + String.format("%-29s", (economyManager.isEconomyEnabled() ? "Enabled (Vault)" : "Disabled")) + "║");
        getLogger().info("║ Folia Mode: " + String.format("%-26s", (isFolia ? "Active" : "Inactive")) + "║");
        getLogger().info("╚════════════════════════════════════════╝");
    }

    public void reload() {
        configManager.loadConfig();
        cooldownManager.saveCooldowns();
        cooldownManager.loadCooldowns();
    }

    // Getters
    public static PokePlugin getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public boolean isFolia() {
        return isFolia;
    }

    public boolean isPaper() {
        return isPaper;
    }

    public boolean isPurpur() {
        return isPurpur;
    }

    public boolean isSpigot() {
        return isSpigot;
    }

    public String getServerType() {
        return serverType;
    }
}