package com.alan.pokePlugin.managers;

import com.alan.pokePlugin.PokePlugin;
import com.alan.pokePlugin.enums.PrivacyMode;
import com.alan.pokePlugin.utils.SchedulerUtil;

import java.io.File;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DatabaseManager {

    private final PokePlugin plugin;
    private Connection connection;
    private final File databaseFile;

    public DatabaseManager(PokePlugin plugin) {
        this.plugin = plugin;
        String dbPath = plugin.getConfigManager().getConfig().getString("database.file", "data/pokedata.db");
        this.databaseFile = new File(plugin.getDataFolder(), dbPath);
    }

    public CompletableFuture<Void> initialize() {
        return SchedulerUtil.runDatabaseTaskVoid(plugin, () -> {
            try {
                if (!databaseFile.getParentFile().exists()) {
                    databaseFile.getParentFile().mkdirs();
                }

                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath());

                createTables();

                plugin.getLogger().info("SQLite database initialized successfully.");
            } catch (ClassNotFoundException | SQLException e) {
                plugin.getLogger().severe("Failed to initialize database: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void createTables() throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS player_settings (
                uuid TEXT PRIMARY KEY,
                privacy_mode TEXT NOT NULL DEFAULT 'ALLOW_ALL',
                blocked_players TEXT DEFAULT ''
            );
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    public CompletableFuture<PrivacyMode> getPrivacyMode(UUID uuid) {
        return SchedulerUtil.runDatabaseTask(plugin, () -> {
            String query = "SELECT privacy_mode FROM player_settings WHERE uuid = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, uuid.toString());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return PrivacyMode.fromString(rs.getString("privacy_mode"));
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to get privacy mode: " + e.getMessage());
            }

            return PrivacyMode.valueOf(
                    plugin.getConfigManager().getConfig().getString("privacy.default-mode", "ALLOW_ALL")
            );
        });
    }

    public CompletableFuture<Void> setPrivacyMode(UUID uuid, PrivacyMode mode) {
        return SchedulerUtil.runDatabaseTaskVoid(plugin, () -> {
            String upsert = """
                INSERT INTO player_settings (uuid, privacy_mode) VALUES (?, ?)
                ON CONFLICT(uuid) DO UPDATE SET privacy_mode = excluded.privacy_mode;
            """;

            try (PreparedStatement stmt = connection.prepareStatement(upsert)) {
                stmt.setString(1, uuid.toString());
                stmt.setString(2, mode.name());
                stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to set privacy mode: " + e.getMessage());
            }
        });
    }

    public CompletableFuture<Set<UUID>> getBlockedPlayers(UUID uuid) {
        return SchedulerUtil.runDatabaseTask(plugin, () -> {
            Set<UUID> blocked = new HashSet<>();
            String query = "SELECT blocked_players FROM player_settings WHERE uuid = ?";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, uuid.toString());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String blockedString = rs.getString("blocked_players");
                    if (blockedString != null && !blockedString.isEmpty()) {
                        for (String blockedUUID : blockedString.split(",")) {
                            try {
                                blocked.add(UUID.fromString(blockedUUID.trim()));
                            } catch (IllegalArgumentException ignored) {
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to get blocked players: " + e.getMessage());
            }

            return blocked;
        });
    }

    public CompletableFuture<Void> blockPlayer(UUID uuid, UUID targetUUID) {
        return getBlockedPlayers(uuid).thenCompose(blocked -> {
            blocked.add(targetUUID);
            return saveBlockedPlayers(uuid, blocked);
        });
    }

    public CompletableFuture<Void> unblockPlayer(UUID uuid, UUID targetUUID) {
        return getBlockedPlayers(uuid).thenCompose(blocked -> {
            blocked.remove(targetUUID);
            return saveBlockedPlayers(uuid, blocked);
        });
    }

    private CompletableFuture<Void> saveBlockedPlayers(UUID uuid, Set<UUID> blocked) {
        return SchedulerUtil.runDatabaseTaskVoid(plugin, () -> {
            String blockedString = blocked.stream()
                    .map(UUID::toString)
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");

            String upsert = """
                INSERT INTO player_settings (uuid, blocked_players) VALUES (?, ?)
                ON CONFLICT(uuid) DO UPDATE SET blocked_players = excluded.blocked_players;
            """;

            try (PreparedStatement stmt = connection.prepareStatement(upsert)) {
                stmt.setString(1, uuid.toString());
                stmt.setString(2, blockedString);
                stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to save blocked players: " + e.getMessage());
            }
        });
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLogger().info("Database connection closed.");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to close database: " + e.getMessage());
        }
    }
}