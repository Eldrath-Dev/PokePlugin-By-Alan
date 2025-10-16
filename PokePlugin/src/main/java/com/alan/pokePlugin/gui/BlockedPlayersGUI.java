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
import java.util.Set;
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
        privacyManager.getBlockedPlayers(player.getUniqueId()).thenAccept(blockedPlayers -> {
            int size = Math.max(27, ((blockedPlayers.size() / 9) + 1) * 9);
            Inventory gui = Bukkit.createInventory(null, size, "§eBlocked Players");

            int slot = 0;
            for (UUID blockedUUID : blockedPlayers) {
                OfflinePlayer blockedPlayer = Bukkit.getOfflinePlayer(blockedUUID);
                gui.setItem(slot++, createPlayerHead(blockedPlayer));
            }

            gui.setItem(size - 5, createBackItem());
            gui.setItem(size - 1, createCloseItem());

            player.openInventory(gui);
        });
    }

    private ItemStack createPlayerHead(OfflinePlayer blockedPlayer) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(blockedPlayer);
        meta.setDisplayName("§e" + blockedPlayer.getName());

        List<String> lore = new ArrayList<>();
        lore.add(messageManager.getMessage("gui.unblock-lore"));
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
            new PokeSettingsGUI(plugin, player).open();
        } else if (slot == size - 1) {
            player.closeInventory();
        } else if (clickedItem != null && clickedItem.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) clickedItem.getItemMeta();
            if (meta.getOwningPlayer() != null) {
                UUID blockedUUID = meta.getOwningPlayer().getUniqueId();
                privacyManager.unblockPlayer(player.getUniqueId(), blockedUUID).thenRun(() -> {
                    player.sendMessage(messageManager.formatMessage("privacy.unblocked", "target", meta.getOwningPlayer().getName()));
                    open();
                });
            }
        }
    }
}