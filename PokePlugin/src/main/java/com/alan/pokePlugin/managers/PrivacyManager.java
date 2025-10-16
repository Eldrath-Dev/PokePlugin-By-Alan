package com.alan.pokePlugin.managers;

import com.alan.pokePlugin.PokePlugin;
import com.alan.pokePlugin.enums.PrivacyMode;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PrivacyManager {

    private final PokePlugin plugin;
    private final DatabaseManager databaseManager;

    public PrivacyManager(PokePlugin plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
    }

    public CompletableFuture<Boolean> canPoke(UUID poker, UUID target) {
        return databaseManager.getPrivacyMode(target).thenCompose(mode -> {
            if (mode == PrivacyMode.ALLOW_ALL) {
                return CompletableFuture.completedFuture(true);
            }

            if (mode == PrivacyMode.DISABLED) {
                return CompletableFuture.completedFuture(false);
            }

            if (mode == PrivacyMode.CUSTOM) {
                return databaseManager.getBlockedPlayers(target).thenApply(blocked -> !blocked.contains(poker));
            }

            return CompletableFuture.completedFuture(true);
        });
    }

    public CompletableFuture<Void> setPrivacyMode(UUID uuid, PrivacyMode mode) {
        return databaseManager.setPrivacyMode(uuid, mode);
    }

    public CompletableFuture<PrivacyMode> getPrivacyMode(UUID uuid) {
        return databaseManager.getPrivacyMode(uuid);
    }

    public CompletableFuture<Void> blockPlayer(UUID uuid, UUID targetUUID) {
        return databaseManager.blockPlayer(uuid, targetUUID).thenRun(() -> {
            setPrivacyMode(uuid, PrivacyMode.CUSTOM);
        });
    }

    public CompletableFuture<Void> unblockPlayer(UUID uuid, UUID targetUUID) {
        return databaseManager.unblockPlayer(uuid, targetUUID);
    }

    public CompletableFuture<Set<UUID>> getBlockedPlayers(UUID uuid) {
        return databaseManager.getBlockedPlayers(uuid);
    }

    public CompletableFuture<Boolean> isBlocked(UUID uuid, UUID targetUUID) {
        return databaseManager.getBlockedPlayers(uuid).thenApply(blocked -> blocked.contains(targetUUID));
    }
}