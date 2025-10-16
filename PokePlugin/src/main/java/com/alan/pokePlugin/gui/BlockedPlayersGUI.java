package com.alan.pokePlugin.gui;

import com.alan.pokePlugin.PokePlugin;
import com.alan.pokePlugin.managers.MessageManager;
import com.alan.pokePlugin.managers.PrivacyManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BlockedPlayersGUI {

    private final PokePlugin plugin;
    private final Player player;
    private final PrivacyManager privacyManager;
    private final MessageManager messageManager;

    public BlockedPlayersGUI(PokePlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.privacyManager = plugin.getPrivacyManager();
        this.messageManager = plugin.getMessageManager();
    }

    public void open() {
        // Fetch blocked players asynchronously
        privacyManager.getBlockedPlayers(player.getUniqueId()).thenAccept(blockedPlayers -> {

            if (plugin.isFolia()) {
                // FOLIA: Use entity scheduler to run on player's region
                player.getScheduler().run(plugin, task -> {
                    openInventory(blockedPlayers);
                }, null);
            } else {
                // BUKKIT/SPIGOT/PAPER/PURPUR: Use main thread scheduler
                Bukkit.getScheduler().runTask(plugin, () -> {
                    openInventory(blockedPlayers);
                });
            }
        });
    }

    private void openInventory(java.util.Set<UUID> blockedPlayers) {
        int size = Math.max(27, ((blockedPlayers.size() / 9) + 1) * 9);
        if (size > 54) size = 54; // Max inventory size

        Inventory gui = Bukkit.createInventory(null, size, "§eBlocked Players");

        int slot = 0;
        for (UUID blockedUUID : blockedPlayers) {
            if (slot >= size - 9) break; // Leave room for control buttons
            OfflinePlayer blockedPlayer = Bukkit.getOfflinePlayer(blockedUUID);
            gui.setItem(slot++, createPlayerHead(blockedPlayer));
        }

        gui.setItem(size - 5, createBackItem());
        gui.setItem(size - 1, createCloseItem());

        player.openInventory(gui);
    }

    private ItemStack createPlayerHead(OfflinePlayer blockedPlayer) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(blockedPlayer);
        meta.setDisplayName("§e" + (blockedPlayer.getName() != null ? blockedPlayer.getName() : "Unknown"));

        List<String> lore = new ArrayList<>();
        lore.add("§7Click to unblock this player");
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createBackItem() {
        ItemStack item = new ItemStack(Material.ARROW);
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(messageManager.getMessage("gui.back"));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createCloseItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(messageManager.getMessage("gui.close"));
        item.setItemMeta(meta);
        return item;
    }

    public void handleClick(int slot, ItemStack clickedItem) {
        int size = player.getOpenInventory().getTopInventory().getSize();

        if (slot == size - 5) {
            // Back button clicked
            if (plugin.isFolia()) {
                // FOLIA: Run on player's region
                player.getScheduler().run(plugin, task -> {
                    new PokeSettingsGUI(plugin, player).open();
                }, null);
            } else {
                // BUKKIT/SPIGOT/PAPER/PURPUR: Run on main thread
                Bukkit.getScheduler().runTask(plugin, () -> {
                    new PokeSettingsGUI(plugin, player).open();
                });
            }
        } else if (slot == size - 1) {
            // Close button clicked
            player.closeInventory();
        } else if (clickedItem != null && clickedItem.getType() == Material.PLAYER_HEAD) {
            // Player head clicked - unblock
            SkullMeta meta = (SkullMeta) clickedItem.getItemMeta();
            if (meta.getOwningPlayer() != null) {
                UUID blockedUUID = meta.getOwningPlayer().getUniqueId();
                String playerName = meta.getOwningPlayer().getName();

                privacyManager.unblockPlayer(player.getUniqueId(), blockedUUID).thenRun(() -> {
                    if (plugin.isFolia()) {
                        // FOLIA: Run on player's region
                        player.getScheduler().run(plugin, task -> {
                            player.sendMessage(messageManager.formatMessage("privacy.unblocked", "target", playerName));
                            open(); // Refresh the GUI
                        }, null);
                    } else {
                        // BUKKIT/SPIGOT/PAPER/PURPUR: Run on main thread
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            player.sendMessage(messageManager.formatMessage("privacy.unblocked", "target", playerName));
                            open(); // Refresh the GUI
                        });
                    }
                });
            }
        }
    }
}