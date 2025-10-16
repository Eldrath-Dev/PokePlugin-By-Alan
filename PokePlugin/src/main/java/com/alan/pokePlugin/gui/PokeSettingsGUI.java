package com.alan.pokePlugin.gui;

import com.alan.pokePlugin.PokePlugin;
import com.alan.pokePlugin.enums.PrivacyMode;
import com.alan.pokePlugin.managers.MessageManager;
import com.alan.pokePlugin.managers.PrivacyManager;
import com.alan.pokePlugin.utils.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PokeSettingsGUI {

    private final PokePlugin plugin;
    private final Player player;
    private final PrivacyManager privacyManager;
    private final MessageManager messageManager;

    public PokeSettingsGUI(PokePlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.privacyManager = plugin.getPrivacyManager();
        this.messageManager = plugin.getMessageManager();
    }

    public void open() {
        plugin.getLogger().info("[" + (plugin.isFolia() ? "Folia" : "Bukkit") + "] Opening GUI for player: " + player.getName());

        // Fetch data asynchronously (works on both Folia and Bukkit)
        privacyManager.getPrivacyMode(player.getUniqueId()).thenAccept(currentMode -> {
            privacyManager.getBlockedPlayers(player.getUniqueId()).thenAccept(blockedPlayers -> {

                if (plugin.isFolia()) {
                    // FOLIA: Use entity scheduler to run on player's region
                    player.getScheduler().run(plugin, task -> {
                        openInventory(currentMode, blockedPlayers.size());
                    }, null);
                } else {
                    // BUKKIT/SPIGOT/PAPER/PURPUR: Use main thread scheduler
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        openInventory(currentMode, blockedPlayers.size());
                    });
                }

            }).exceptionally(ex -> {
                plugin.getLogger().severe("Error getting blocked players: " + ex.getMessage());
                ex.printStackTrace();
                return null;
            });
        }).exceptionally(ex -> {
            plugin.getLogger().severe("Error getting privacy mode: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        });
    }

    private void openInventory(PrivacyMode currentMode, int blockedCount) {
        try {
            int size = plugin.getConfigManager().getConfig().getInt("gui.size", 27);
            String title = plugin.getConfigManager().getConfig().getString("gui.title", "&aPoke Settings")
                    .replace("&", "§");

            plugin.getLogger().info("[" + (plugin.isFolia() ? "Folia" : "Bukkit") + "] Creating inventory with title: " + title);
            Inventory gui = Bukkit.createInventory(null, size, title);

            gui.setItem(11, createAllowAllItem(currentMode));
            gui.setItem(13, createDisableAllItem(currentMode));
            gui.setItem(15, createBlockedPlayersItem(blockedCount));
            gui.setItem(size - 1, createCloseItem());

            player.openInventory(gui);
            plugin.getLogger().info("[" + (plugin.isFolia() ? "Folia" : "Bukkit") + "] GUI opened successfully for: " + player.getName());
        } catch (Exception e) {
            plugin.getLogger().severe("Error creating GUI inventory: " + e.getMessage());
            e.printStackTrace();
            player.sendMessage("§cError opening GUI. Check console for details.");
        }
    }

    private ItemStack createAllowAllItem(PrivacyMode currentMode) {
        ItemStack item = new ItemStack(Material.LIME_DYE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(messageManager.getMessage("settings.allow"));

        String status = currentMode == PrivacyMode.ALLOW_ALL ? "§aEnabled" : "§cDisabled";
        List<String> lore = new ArrayList<>();
        lore.add("§7Click to allow pokes from everyone");
        lore.add("");
        lore.add("§eCurrent: " + status);
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createDisableAllItem(PrivacyMode currentMode) {
        ItemStack item = new ItemStack(Material.RED_DYE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(messageManager.getMessage("settings.disable"));

        String status = currentMode == PrivacyMode.DISABLED ? "§aEnabled" : "§cDisabled";
        List<String> lore = new ArrayList<>();
        lore.add("§7Click to disable all pokes");
        lore.add("");
        lore.add("§eCurrent: " + status);
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createBlockedPlayersItem(int blockedCount) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(messageManager.getMessage("settings.blocked-players"));

        List<String> lore = new ArrayList<>();
        lore.add("§7Click to manage blocked players");
        lore.add("");
        lore.add("§eBlocked: " + blockedCount);
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createCloseItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(messageManager.getMessage("gui.close"));
        item.setItemMeta(meta);
        return item;
    }

    public void handleClick(int slot) {
        plugin.getLogger().info("[" + (plugin.isFolia() ? "Folia" : "Bukkit") + "] GUI click at slot: " + slot + " by player: " + player.getName());

        if (slot == 11) {
            // Allow All clicked
            privacyManager.setPrivacyMode(player.getUniqueId(), PrivacyMode.ALLOW_ALL).thenRun(() -> {
                if (plugin.isFolia()) {
                    // FOLIA: Run on player's region
                    player.getScheduler().run(plugin, task -> {
                        player.sendMessage(messageManager.formatMessage("settings.mode-changed", "mode", "Allow All"));
                        player.closeInventory();
                    }, null);
                } else {
                    // BUKKIT/SPIGOT/PAPER/PURPUR: Run on main thread
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.sendMessage(messageManager.formatMessage("settings.mode-changed", "mode", "Allow All"));
                        player.closeInventory();
                    });
                }
            });
        } else if (slot == 13) {
            // Disable All clicked
            privacyManager.setPrivacyMode(player.getUniqueId(), PrivacyMode.DISABLED).thenRun(() -> {
                if (plugin.isFolia()) {
                    // FOLIA: Run on player's region
                    player.getScheduler().run(plugin, task -> {
                        player.sendMessage(messageManager.formatMessage("settings.mode-changed", "mode", "Disabled"));
                        player.closeInventory();
                    }, null);
                } else {
                    // BUKKIT/SPIGOT/PAPER/PURPUR: Run on main thread
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.sendMessage(messageManager.formatMessage("settings.mode-changed", "mode", "Disabled"));
                        player.closeInventory();
                    });
                }
            });
        } else if (slot == 15) {
            // Blocked Players clicked
            if (plugin.isFolia()) {
                // FOLIA: Run on player's region
                player.getScheduler().run(plugin, task -> {
                    new BlockedPlayersGUI(plugin, player).open();
                }, null);
            } else {
                // BUKKIT/SPIGOT/PAPER/PURPUR: Run on main thread
                Bukkit.getScheduler().runTask(plugin, () -> {
                    new BlockedPlayersGUI(plugin, player).open();
                });
            }
        } else if (slot == plugin.getConfigManager().getConfig().getInt("gui.size", 27) - 1) {
            // Close button clicked
            player.closeInventory();
        }
    }
}