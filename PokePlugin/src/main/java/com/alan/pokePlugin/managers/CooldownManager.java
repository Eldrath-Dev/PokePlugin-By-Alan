package com.alan.pokePlugin.managers;

import com.alan.pokePlugin.PokePlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final PokePlugin plugin;
    private final File cooldownFile;
    private FileConfiguration cooldownConfig;
    private final Map<UUID, Long> cooldowns;

    public CooldownManager(PokePlugin plugin) {
        this.plugin = plugin;
        this.cooldownFile = new File(plugin.getDataFolder(), "cooldowns.yml");
        this.cooldowns = new HashMap<>();
    }

    public void loadCooldowns() {
        if (!cooldownFile.exists()) {
            try {
                cooldownFile.getParentFile().mkdirs();
                cooldownFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create cooldowns.yml: " + e.getMessage());
            }
        }

        cooldownConfig = YamlConfiguration.loadConfiguration(cooldownFile);
        cooldowns.clear();

        if (cooldownConfig.contains("cooldowns")) {
            for (String key : cooldownConfig.getConfigurationSection("cooldowns").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    long timestamp = cooldownConfig.getLong("cooldowns." + key);
                    cooldowns.put(uuid, timestamp);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in cooldowns.yml: " + key);
                }
            }
        }

        plugin.getLogger().info("Loaded " + cooldowns.size() + " cooldowns from storage.");
    }

    public void saveCooldowns() {
        cooldownConfig = new YamlConfiguration();

        for (Map.Entry<UUID, Long> entry : cooldowns.entrySet()) {
            cooldownConfig.set("cooldowns." + entry.getKey().toString(), entry.getValue());
        }

        try {
            cooldownConfig.save(cooldownFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save cooldowns.yml: " + e.getMessage());
        }
    }

    public void setCooldown(UUID playerUUID) {
        long cooldownSeconds = plugin.getConfigManager().getCooldownHours() * 3600L;
        long expiryTime = System.currentTimeMillis() / 1000 + cooldownSeconds;
        cooldowns.put(playerUUID, expiryTime);
    }

    public long getRemainingCooldown(UUID playerUUID) {
        if (!cooldowns.containsKey(playerUUID)) {
            return 0;
        }

        long expiryTime = cooldowns.get(playerUUID);
        long currentTime = System.currentTimeMillis() / 1000;
        long remaining = expiryTime - currentTime;

        if (remaining <= 0) {
            cooldowns.remove(playerUUID);
            return 0;
        }

        return remaining;
    }

    public void clearCooldown(UUID playerUUID) {
        cooldowns.remove(playerUUID);
    }

    public void clearAllCooldowns() {
        cooldowns.clear();
    }

    public Map<UUID, Long> getAllCooldowns() {
        return new HashMap<>(cooldowns);
    }
}