package com.alan.pokePlugin.gui;

import com.alan.pokePlugin.PokePlugin;
import com.alan.pokePlugin.enums.PrivacyMode;
import com.alan.pokePlugin.managers.MessageManager;
import com.alan.pokePlugin.managers.PrivacyManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        int size = plugin.getConfigManager().getConfig().getInt("gui.size", 27);
        String title = plugin.getConfigManager().getConfig().getString("gui.title", "&aPoke Settings")
                .replace("&", "§");

        Inventory gui = Bukkit.createInventory(null, size, title);

        privacyManager.getPrivacyMode(player.getUniqueId()).thenAccept(currentMode -> {
            privacyManager.getBlockedPlayers(player.getUniqueId()).thenAccept(blockedPlayers -> {
                gui.setItem(11, createAllowAllItem(currentMode));
                gui.setItem(13, createDisableAllItem(currentMode));
                gui.setItem(15, createBlockedPlayersItem(blockedPlayers.size()));

                gui.setItem(size - 1, createCloseItem());

                player.openInventory(gui);
            });
        });
    }

    private ItemStack createAllowAllItem(PrivacyMode currentMode) {
        ItemStack item = new ItemStack(Material.LIME_DYE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(messageManager.getMessage("settings.allow"));

        String status = currentMode == PrivacyMode.ALLOW_ALL ? "§aEnabled" : "§cDisabled";
        List<String> lore = messageManager.getMessageList("gui.allow-all-lore");
        lore = lore.stream()
                .map(s -> s.replace("{status}", status))
                .toList();
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createDisableAllItem(PrivacyMode currentMode) {
        ItemStack item = new ItemStack(Material.RED_DYE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(messageManager.getMessage("settings.disable"));

        String status = currentMode == PrivacyMode.DISABLED ? "§aEnabled" : "§cDisabled";
        List<String> lore = messageManager.getMessageList("gui.disable-all-lore");
        lore = lore.stream()
                .map(s -> s.replace("{status}", status))
                .toList();
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createBlockedPlayersItem(int blockedCount) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(messageManager.getMessage("settings.blocked-players"));

        List<String> lore = messageManager.getMessageList("gui.blocked-players-lore");
        lore = lore.stream()
                .map(s -> s.replace("{count}", String.valueOf(blockedCount)))
                .toList();
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
        if (slot == 11) {
            privacyManager.setPrivacyMode(player.getUniqueId(), PrivacyMode.ALLOW_ALL).thenRun(() -> {
                player.sendMessage(messageManager.formatMessage("settings.mode-changed", "mode", "Allow All"));
                player.closeInventory();
            });
        } else if (slot == 13) {
            privacyManager.setPrivacyMode(player.getUniqueId(), PrivacyMode.DISABLED).thenRun(() -> {
                player.sendMessage(messageManager.formatMessage("settings.mode-changed", "mode", "Disabled"));
                player.closeInventory();
            });
        } else if (slot == 15) {
            new BlockedPlayersGUI(plugin, player).open();
        } else if (slot == plugin.getConfigManager().getConfig().getInt("gui.size", 27) - 1) {
            player.closeInventory();
        }
    }
}