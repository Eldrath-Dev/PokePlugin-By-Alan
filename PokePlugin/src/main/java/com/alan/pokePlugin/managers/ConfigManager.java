package com.alan.pokePlugin.managers;

import com.alan.pokePlugin.PokePlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final PokePlugin plugin;
    private FileConfiguration config;

    public ConfigManager(PokePlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public double getPokeCost() {
        return config.getDouble("poke-cost", 50.0);
    }

    public int getCooldownHours() {
        return config.getInt("cooldown-hours", 3);
    }

    public String getMessage(String key) {
        return config.getString("messages." + key, "§cMessage not found: " + key)
                .replace("&", "§");
    }

    public boolean isTitleEnabled() {
        return config.getBoolean("notifications.title", true);
    }

    public boolean isSoundEnabled() {
        return config.getBoolean("notifications.sound", true);
    }

    public String getSoundType() {
        return config.getString("notifications.sound-type", "ENTITY_EXPERIENCE_ORB_PICKUP");
    }

    public double getSoundVolume() {
        return config.getDouble("notifications.sound-volume", 1.0);
    }

    public double getSoundPitch() {
        return config.getDouble("notifications.sound-pitch", 1.0);
    }

    public String getTitleMain() {
        return config.getString("title.main", "§b§lPOKED!")
                .replace("&", "§");
    }

    public String getTitleSubtitle() {
        return config.getString("title.subtitle", "§eby %player%")
                .replace("&", "§");
    }

    public int getTitleFadeIn() {
        return config.getInt("title.fade-in", 10);
    }

    public int getTitleStay() {
        return config.getInt("title.stay", 40);
    }

    public int getTitleFadeOut() {
        return config.getInt("title.fade-out", 10);
    }

    public FileConfiguration getConfig() {
        return config;
    }
}