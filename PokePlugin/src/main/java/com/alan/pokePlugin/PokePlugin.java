package com.alan.pokePlugin;

import com.alan.pokePlugin.commands.PokeCommand;
import com.alan.pokePlugin.gui.BlockedPlayersGUI;
import com.alan.pokePlugin.gui.PokeSettingsGUI;
import com.alan.pokePlugin.managers.*;
import com.alan.pokePlugin.placeholders.PokePlaceholderExpansion;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PokePlugin extends JavaPlugin implements Listener {

    private static PokePlugin instance;
    private ConfigManager configManager;
    private CooldownManager cooldownManager;
    private EconomyManager economyManager;
    private DatabaseManager databaseManager;
    private PrivacyManager privacyManager;
    private MessageManager messageManager;

    // Platform detection
    private boolean isFolia = false;
    private boolean isPaper = false;
    private boolean isPurpur = false;
    private boolean isSpigot = false;
    private String serverType = "Bukkit";

    @Override
    public void onEnable() {
        instance = this;

        // Step 1: Detect server platform
        detectServerPlatform();
        logPlatformDetection();

        // Step 2: Initialize configuration
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        // Step 3: Initialize message system
        messageManager = new MessageManager(this);

        // Step 4: Initialize database (async)
        databaseManager = new DatabaseManager(this);

        if (isFolia) {
            // FOLIA: Initialize database on async scheduler
            Bukkit.getAsyncScheduler().runNow(this, task -> {
                databaseManager.initialize().join();
                getLogger().info("[Folia] Database initialized on async scheduler");
            });
        } else {
            // BUKKIT/SPIGOT/PAPER/PURPUR: Initialize database normally
            databaseManager.initialize().join();
            getLogger().info("[Bukkit] Database initialized on main thread");
        }

        // Step 5: Initialize privacy manager
        privacyManager = new PrivacyManager(this);

        // Step 6: Initialize cooldown manager
        cooldownManager = new CooldownManager(this);
        cooldownManager.loadCooldowns();

        // Step 7: Initialize economy manager
        economyManager = new EconomyManager(this);
        economyManager.setupEconomy();

        // Step 8: Register commands
        registerCommands();

        // Step 9: Register event listeners
        registerListeners();

        // Step 10: Setup PlaceholderAPI
        setupPlaceholderAPI();

        // Step 11: Setup bStats metrics
        setupMetrics();

        // Step 12: Log startup information
        logStartupInfo();
    }

    @Override
    public void onDisable() {
        getLogger().info("Shutting down PokePlugin...");

        // Save cooldowns
        if (cooldownManager != null) {
            cooldownManager.saveCooldowns();
            getLogger().info("Cooldowns saved successfully.");
        }

        // Close database connection
        if (databaseManager != null) {
            databaseManager.close();
            getLogger().info("Database connection closed.");
        }

        getLogger().info("PokePlugin has been disabled!");
    }

    /**
     * Detect server platform (Folia, Purpur, Paper, Spigot, Bukkit)
     */
    private void detectServerPlatform() {
        // Check for Folia first (most specific)
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            isFolia = true;
            isPaper = true; // Folia is based on Paper
            serverType = "Folia";
            return;
        } catch (ClassNotFoundException ignored) {
        }

        // Check for Purpur
        try {
            Class.forName("org.purpurmc.purpur.PurpurConfig");
            isPurpur = true;
            isPaper = true; // Purpur extends Paper
            serverType = "Purpur";
            return;
        } catch (ClassNotFoundException ignored) {
        }

        // Check for Paper (old config)
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            isPaper = true;
            serverType = "Paper";
            return;
        } catch (ClassNotFoundException ignored) {
        }

        // Check for Paper (new config - 1.19+)
        try {
            Class.forName("io.papermc.paper.configuration.Configuration");
            isPaper = true;
            serverType = "Paper";
            return;
        } catch (ClassNotFoundException ignored) {
        }

        // Check for Spigot
        try {
            Class.forName("org.spigotmc.SpigotConfig");
            isSpigot = true;
            serverType = "Spigot";
            return;
        } catch (ClassNotFoundException ignored) {
        }

        // Fallback to Bukkit
        serverType = "Bukkit";
    }

    /**
     * Log platform detection results
     */
    private void logPlatformDetection() {
        getLogger().info("╔════════════════════════════════════════╗");
        getLogger().info("║      Platform Detection Results       ║");
        getLogger().info("╠════════════════════════════════════════╣");
        getLogger().info(String.format("║ Detected: %-28s ║", serverType));
        getLogger().info(String.format("║ Folia Mode: %-26s ║", (isFolia ? "Active" : "Inactive")));
        getLogger().info(String.format("║ Paper Features: %-23s ║", (isPaper ? "Available" : "Unavailable")));
        getLogger().info("╚════════════════════════════════════════╝");
    }

    /**
     * Register commands
     */
    private void registerCommands() {
        PokeCommand pokeCommand = new PokeCommand(this);
        getCommand("poke").setExecutor(pokeCommand);
        getCommand("poke").setTabCompleter(pokeCommand);
        getLogger().info("Commands registered successfully!");
    }

    /**
     * Register event listeners
     */
    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("Event listeners registered successfully!");
    }

    /**
     * Setup PlaceholderAPI integration
     */
    private void setupPlaceholderAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            if (configManager.getConfig().getBoolean("placeholders.enabled", true)) {
                try {
                    new PokePlaceholderExpansion(this).register();
                    getLogger().info("PlaceholderAPI hook registered successfully!");
                } catch (Exception e) {
                    getLogger().warning("Failed to register PlaceholderAPI expansion: " + e.getMessage());
                }
            } else {
                getLogger().info("PlaceholderAPI integration disabled in config.");
            }
        } else {
            getLogger().info("PlaceholderAPI not found. Placeholder support disabled.");
        }
    }

    /**
     * Setup bStats metrics
     */
    private void setupMetrics() {
        if (configManager.getConfig().getBoolean("metrics.enabled", true)) {
            try {
                new Metrics(this, 27606);
                getLogger().info("bStats metrics enabled (Plugin ID: 27606)");
            } catch (Exception e) {
                getLogger().warning("Failed to initialize bStats: " + e.getMessage());
            }
        } else {
            getLogger().info("bStats metrics disabled in config.");
        }
    }

    /**
     * Log startup information with fancy formatting
     */
    private void logStartupInfo() {
        String version = getDescription().getVersion();
        String mcVersion = Bukkit.getVersion().split("-")[0];
        boolean economyEnabled = economyManager.isEconomyEnabled();
        String economyProvider = economyEnabled ? economyManager.getEconomy().getName() : "None";
        boolean metricsEnabled = configManager.getConfig().getBoolean("metrics.enabled", true);
        boolean placeholdersEnabled = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null
                && configManager.getConfig().getBoolean("placeholders.enabled", true);

        getLogger().info("╔══════════════════════════════════════════════════╗");
        getLogger().info(String.format("║         PokePlugin v%-28s║", version));
        getLogger().info(String.format("║         Public Brand: %-27s║", "PokeNotify"));
        getLogger().info("╠══════════════════════════════════════════════════╣");
        getLogger().info(String.format("║ Server Type: %-40s║", serverType));
        getLogger().info(String.format("║ Server Version: %-37s║", mcVersion));
        getLogger().info(String.format("║ Java Version: %-39s║", System.getProperty("java.version")));
        getLogger().info("╠══════════════════════════════════════════════════╣");
        getLogger().info(String.format("║ Economy: %-44s║", economyEnabled ? "Enabled (" + economyProvider + ")" : "Disabled"));
        getLogger().info(String.format("║ PlaceholderAPI: %-37s║", placeholdersEnabled ? "Enabled" : "Disabled"));
        getLogger().info(String.format("║ Metrics: %-44s║", metricsEnabled ? "Enabled (bStats ID 27606)" : "Disabled"));
        getLogger().info(String.format("║ Database: %-43s║", "SQLite"));
        getLogger().info("╠══════════════════════════════════════════════════╣");
        getLogger().info(String.format("║ Scheduler Mode: %-37s║", isFolia ? "Folia (Regionized)" : "Bukkit (Main Thread)"));
        getLogger().info(String.format("║ Threading: %-42s║", isFolia ? "Region-Based" : "Single-Threaded"));
        getLogger().info("╠══════════════════════════════════════════════════╣");
        getLogger().info("║ Author: AlanTheDev                               ║");
        getLogger().info("║ GitHub: https://github.com/Eldrath-Dev           ║");
        getLogger().info("╚══════════════════════════════════════════════════╝");
    }

    /**
     * Handle inventory click events (GUI system)
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        if (!(event.getWhoClicked() instanceof org.bukkit.entity.Player player)) return;

        String title = event.getView().getTitle();

        // Strip color codes for comparison
        String strippedTitle = title.replaceAll("§[0-9a-fk-or]", "");

        // Debug logging
        if (configManager.getConfig().getBoolean("debug", false)) {
            getLogger().info("[" + serverType + "] Inventory clicked: " + strippedTitle + " by " + player.getName());
        }

        // Handle Poke Settings GUI
        if (strippedTitle.contains("Poke Settings")) {
            event.setCancelled(true);

            if (isFolia) {
                // FOLIA: Run on player's region
                player.getScheduler().run(this, task -> {
                    PokeSettingsGUI gui = new PokeSettingsGUI(this, player);
                    gui.handleClick(event.getSlot());
                }, null);
            } else {
                // BUKKIT/SPIGOT/PAPER/PURPUR: Run on main thread
                Bukkit.getScheduler().runTask(this, () -> {
                    PokeSettingsGUI gui = new PokeSettingsGUI(this, player);
                    gui.handleClick(event.getSlot());
                });
            }
            return;
        }

        // Handle Blocked Players GUI
        if (strippedTitle.contains("Blocked Players")) {
            event.setCancelled(true);

            if (isFolia) {
                // FOLIA: Run on player's region
                player.getScheduler().run(this, task -> {
                    BlockedPlayersGUI gui = new BlockedPlayersGUI(this, player);
                    gui.handleClick(event.getSlot(), event.getCurrentItem());
                }, null);
            } else {
                // BUKKIT/SPIGOT/PAPER/PURPUR: Run on main thread
                Bukkit.getScheduler().runTask(this, () -> {
                    BlockedPlayersGUI gui = new BlockedPlayersGUI(this, player);
                    gui.handleClick(event.getSlot(), event.getCurrentItem());
                });
            }
        }
    }

    /**
     * Reload plugin configuration and data
     */
    public void reload() {
        getLogger().info("Reloading PokePlugin configuration...");

        // Reload config
        configManager.loadConfig();
        getLogger().info("Config reloaded.");

        // Reload messages
        messageManager.reloadMessages();
        getLogger().info("Messages reloaded.");

        // Save and reload cooldowns
        cooldownManager.saveCooldowns();
        cooldownManager.loadCooldowns();
        getLogger().info("Cooldowns reloaded.");

        getLogger().info("PokePlugin reload complete!");
    }

    // ==================== GETTERS ====================

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

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public PrivacyManager getPrivacyManager() {
        return privacyManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
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

    /**
     * Check if running on Bukkit (not Spigot/Paper/etc)
     */
    public boolean isBukkit() {
        return !isSpigot && !isPaper && !isPurpur && !isFolia;
    }

    /**
     * Check if server supports modern Paper features
     */
    public boolean hasPaperFeatures() {
        return isPaper || isPurpur || isFolia;
    }

    /**
     * Get the platform name for display
     */
    public String getPlatformName() {
        if (isFolia) return "Folia (Regionized)";
        if (isPurpur) return "Purpur (Paper Fork)";
        if (isPaper) return "Paper";
        if (isSpigot) return "Spigot";
        return "Bukkit";
    }
}