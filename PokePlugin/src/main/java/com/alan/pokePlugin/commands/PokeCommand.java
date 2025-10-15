package com.alan.pokePlugin.commands;

import com.alan.pokePlugin.PokePlugin;
import com.alan.pokePlugin.managers.ConfigManager;
import com.alan.pokePlugin.managers.CooldownManager;
import com.alan.pokePlugin.managers.EconomyManager;
import com.alan.pokePlugin.utils.SchedulerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PokeCommand implements CommandExecutor, TabCompleter {

    private final PokePlugin plugin;
    private final ConfigManager configManager;
    private final CooldownManager cooldownManager;
    private final EconomyManager economyManager;

    public PokeCommand(PokePlugin plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.cooldownManager = plugin.getCooldownManager();
        this.economyManager = plugin.getEconomyManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§c╔════════════════════════════════╗");
            sender.sendMessage("§c║      §6PokePlugin Commands      §c║");
            sender.sendMessage("§c╠════════════════════════════════╣");
            sender.sendMessage("§c║ §e/poke <player> §7- Poke player §c║");
            sender.sendMessage("§c║ §e/poke reload §7- Reload config §c║");
            sender.sendMessage("§c╚════════════════════════════════╝");
            return true;
        }

        // Handle reload command
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("poke.reload")) {
                sender.sendMessage(configManager.getMessage("no-permission"));
                return true;
            }
            plugin.reload();
            sender.sendMessage(configManager.getMessage("reload-success"));
            return true;
        }

        // Only players can poke
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can poke other players!");
            return true;
        }

        Player player = (Player) sender;

        // Check permission
        if (!player.hasPermission("poke.use")) {
            player.sendMessage(configManager.getMessage("no-permission"));
            return true;
        }

        // Get target player
        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        // Validate target
        if (target == null || !target.isOnline()) {
            player.sendMessage(configManager.getMessage("player-not-found"));
            return true;
        }

        // Prevent self-poke
        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(configManager.getMessage("self-poke"));
            return true;
        }

        // Check cooldown (unless bypassed)
        if (!player.hasPermission("poke.bypass.cooldown")) {
            long remainingTime = cooldownManager.getRemainingCooldown(player.getUniqueId());
            if (remainingTime > 0) {
                String timeFormatted = formatTime(remainingTime);
                String message = configManager.getMessage("cooldown")
                        .replace("%time%", timeFormatted);
                player.sendMessage(message);
                return true;
            }
        }

        // Check economy cost (unless bypassed)
        if (!player.hasPermission("poke.bypass.cost")) {
            double cost = configManager.getPokeCost();
            if (economyManager.isEconomyEnabled()) {
                if (!economyManager.hasBalance(player, cost)) {
                    String message = configManager.getMessage("no-balance")
                            .replace("%cost%", String.valueOf((int) cost));
                    player.sendMessage(message);
                    return true;
                }
                // Withdraw money
                economyManager.withdraw(player, cost);
            }
        }

        // Set cooldown
        cooldownManager.setCooldown(player.getUniqueId());

        // Execute the poke
        executePoke(player, target);

        return true;
    }

    private void executePoke(Player poker, Player target) {
        String pokedMessage = configManager.getMessage("poked")
                .replace("%player%", poker.getName());
        String successMessage = configManager.getMessage("poke-success")
                .replace("%target%", target.getName());

        // Send notification to target player
        SchedulerUtil.runTask(plugin, target, () -> {
            target.sendMessage(pokedMessage);

            // Play sound if enabled
            if (configManager.isSoundEnabled()) {
                try {
                    Sound sound = Sound.valueOf(configManager.getSoundType());
                    target.playSound(
                            target.getLocation(),
                            sound,
                            (float) configManager.getSoundVolume(),
                            (float) configManager.getSoundPitch()
                    );
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid sound type in config: " + configManager.getSoundType());
                    // Fallback to default sound
                    try {
                        target.playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                    } catch (Exception ignored) {
                    }
                }
            }

            // Show title if enabled (Adventure API - works on Paper 1.16.5+)
            if (configManager.isTitleEnabled()) {
                try {
                    String mainTitle = configManager.getTitleMain().replace("%player%", poker.getName());
                    String subtitle = configManager.getTitleSubtitle().replace("%player%", poker.getName());

                    Title title = Title.title(
                            Component.text(mainTitle),
                            Component.text(subtitle),
                            Title.Times.times(
                                    Duration.ofMillis(configManager.getTitleFadeIn() * 50L),
                                    Duration.ofMillis(configManager.getTitleStay() * 50L),
                                    Duration.ofMillis(configManager.getTitleFadeOut() * 50L)
                            )
                    );
                    target.showTitle(title);
                } catch (Exception e) {
                    // Fallback for older versions or if Adventure API isn't available
                    plugin.getLogger().warning("Could not send title (Adventure API not available or incompatible)");
                }
            }
        });

        // Send confirmation to poker
        SchedulerUtil.runTask(plugin, poker, () -> {
            poker.sendMessage(successMessage);
        });
    }

    private String formatTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, secs);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, secs);
        } else {
            return String.format("%ds", secs);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Add reload option for ops
            if (sender.hasPermission("poke.reload")) {
                completions.add("reload");
            }

            // Add all online players
            completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList()));

            // Filter based on what user typed
            return completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .sorted()
                    .collect(Collectors.toList());
        }

        return completions;
    }
}