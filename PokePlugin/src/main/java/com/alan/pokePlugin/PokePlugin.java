package com.alan.pokePlugin;

import com.alan.pokePlugin.commands.PokeCommand;
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
    private boolean isFolia;
    private String serverType;

    @Override
    public void onEnable() {
        instance = this;

        detectServerPlatform();

        configManager = new ConfigManager(this);
        configManager.loadConfig();

        messageManager = new MessageManager(this);

        databaseManager = new DatabaseManager(this);
        databaseManager.initialize().join();

        privacyManager = new PrivacyManager(this);

        cooldownManager = new CooldownManager(this);
        cooldownManager.loadCooldowns();

        economyManager = new EconomyManager(this);
        economyManager.setupEconomy();

        registerCommands();
        registerListeners();

        setupPlaceholderAPI();
        setupMetrics();

        logStartupInfo();
    }

    @Override
    public void onDisable() {
        if (cooldownManager != null) {
            cooldownManager.saveCooldowns();
        }
        if (databaseManager != null) {
            databaseManager.close();
        }
        getLogger().info("PokePlugin has been disabled!");
    }

    private void detectServerPlatform() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            isFolia = true;
            serverType = "Folia";
            return;
        } catch (ClassNotFoundException ignored) {
        }

        try {
            Class.forName("org.purpurmc.purpur.PurpurConfig");
            serverType = "Purpur";
            return;
        } catch (ClassNotFoundException ignored) {
        }

        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            serverType = "Paper";
            return;
        } catch (ClassNotFoundException ignored) {
        }

        try {
            Class.forName("io.papermc.paper.configuration.Configuration");
            serverType = "Paper";
            return;
        } catch (ClassNotFoundException ignored) {
        }

        try {
            Class.forName("org.spigotmc.SpigotConfig");
            serverType = "Spigot";
            return;
        } catch (ClassNotFoundException ignored) {
        }

        serverType = "Bukkit";
    }

    private void registerCommands() {
        PokeCommand pokeCommand = new PokeCommand(this);
        getCommand("poke").setExecutor(pokeCommand);
        getCommand("poke").setTabCompleter(pokeCommand);
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    private void setupPlaceholderAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            if (configManager.getConfig().getBoolean("placeholders.enabled", true)) {
                new PokePlaceholderExpansion(this).register();
                getLogger().info("PlaceholderAPI hook registered successfully!");
            }
        }
    }

    private void setupMetrics() {
        if (configManager.getConfig().getBoolean("metrics.enabled", true)) {
            new Metrics(this, 27606);
            getLogger().info("bStats metrics enabled (ID: 27606)");
        }
    }

    private void logStartupInfo() {
        String version = Bukkit.getVersion().split("-")[0];
        boolean economyEnabled = economyManager.isEconomyEnabled();
        boolean metricsEnabled = configManager.getConfig().getBoolean("metrics.enabled", true);

        getLogger().info("╔══════════════════════════════════════════════════╗");
        getLogger().info(String.format("║               PokePlugin v%-23s║", getDescription().getVersion()));
        getLogger().info("╠══════════════════════════════════════════════════╣");
        getLogger().info(String.format("║ Server Type: %-40s║", serverType));
        getLogger().info(String.format("║ Server Version: %-37s║", version));
        getLogger().info(String.format("║ Economy: %-44s║", (economyEnabled ? "Enabled (Vault)" : "Disabled")));
        getLogger().info(String.format("║ Metrics: %-44s║", (metricsEnabled ? "Enabled (bStats ID 27606)" : "Disabled")));
        getLogger().info("╚══════════════════════════════════════════════════╝");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().contains("Poke Settings")) {
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof org.bukkit.entity.Player player) {
                com.alan.pokePlugin.gui.PokeSettingsGUI gui = new com.alan.pokePlugin.gui.PokeSettingsGUI(this, player);
                gui.handleClick(event.getSlot());
            }
        } else if (event.getView().getTitle().contains("Blocked Players")) {
            event.setCancelled(true);
            if (event.getWhoClicked() instanceof org.bukkit.entity.Player player) {
                com.alan.pokePlugin.gui.BlockedPlayersGUI gui = new com.alan.pokePlugin.gui.BlockedPlayersGUI(this, player);
                gui.handleClick(event.getSlot(), event.getCurrentItem());
            }
        }
    }

    public void reload() {
        configManager.loadConfig();
        messageManager.reloadMessages();
        cooldownManager.saveCooldowns();
        cooldownManager.loadCooldowns();
    }

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

    public String getServerType() {
        return serverType;
    }
}