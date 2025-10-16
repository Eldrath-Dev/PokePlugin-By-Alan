package com.alan.pokePlugin.placeholders;

import com.alan.pokePlugin.PokePlugin;
import com.alan.pokePlugin.enums.PrivacyMode;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PokePlaceholderExpansion extends PlaceholderExpansion {

    private final PokePlugin plugin;

    public PokePlaceholderExpansion(PokePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "poke";
    }

    @Override
    public @NotNull String getAuthor() {
        return "AlanTheDev";
    }

    @Override
    public @NotNull String getVersion() {
        return "2.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        if (params.equalsIgnoreCase("status")) {
            PrivacyMode mode = plugin.getPrivacyManager().getPrivacyMode(player.getUniqueId()).join();
            return mode == PrivacyMode.DISABLED ? "Disabled" : "Enabled";
        }

        if (params.equalsIgnoreCase("privacy")) {
            PrivacyMode mode = plugin.getPrivacyManager().getPrivacyMode(player.getUniqueId()).join();
            return mode.getDisplayName();
        }

        return null;
    }
}