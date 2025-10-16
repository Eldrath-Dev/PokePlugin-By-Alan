package com.alan.pokePlugin.commands;

import com.alan.pokePlugin.PokePlugin;
import com.alan.pokePlugin.enums.PrivacyMode;
import com.alan.pokePlugin.gui.PokeSettingsGUI;
import com.alan.pokePlugin.managers.*;
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
import java.util.UUID;
import java.util.stream.Collectors;

public class PokeCommand implements CommandExecutor, TabCompleter {

    private final PokePlugin plugin;
    private final ConfigManager configManager;
    private final CooldownManager cooldownManager;
    private final EconomyManager economyManager;
    private final PrivacyManager privacyManager;
    private final MessageManager messageManager;

    public PokeCommand(PokePlugin plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.cooldownManager = plugin.getCooldownManager();
        this.economyManager = plugin.getEconomyManager();
        this.privacyManager = plugin.getPrivacyManager();
        this.messageManager = plugin.getMessageManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(messageManager.getMessage("errors.command-usage"));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("pokeplugin.reload")) {
                sender.sendMessage(messageManager.getMessage("poke.no-permission"));
                return true;
            }
            plugin.reload();
            sender.sendMessage(messageManager.getMessage("admin.reload"));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        if (args[0].equalsIgnoreCase("settings")) {
            if (!player.hasPermission("pokeplugin.settings")) {
                player.sendMessage(messageManager.getMessage("poke.no-permission"));
                return true;
            }
            new PokeSettingsGUI(plugin, player).open();
            return true;
        }

        if (args[0].equalsIgnoreCase("allow")) {
            privacyManager.setPrivacyMode(player.getUniqueId(), PrivacyMode.ALLOW_ALL).thenRun(() -> {
                player.sendMessage(messageManager.formatMessage("settings.mode-changed", "mode", "Allow All"));
            });
            return true;
        }

        if (args[0].equalsIgnoreCase("disable")) {
            privacyManager.setPrivacyMode(player.getUniqueId(), PrivacyMode.DISABLED).thenRun(() -> {
                player.sendMessage(messageManager.formatMessage("settings.mode-changed", "mode", "Disabled"));
            });
            return true;
        }

        if (args[0].equalsIgnoreCase("block")) {
            if (args.length < 2) {
                player.sendMessage("§cUsage: /poke block <player>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(messageManager.getMessage("poke.player-not-found"));
                return true;
            }
            if (target.getUniqueId().equals(player.getUniqueId())) {
                player.sendMessage(messageManager.getMessage("privacy.block-self"));
                return true;
            }
            privacyManager.blockPlayer(player.getUniqueId(), target.getUniqueId()).thenRun(() -> {
                player.sendMessage(messageManager.formatMessage("privacy.blocked", "target", target.getName()));
            });
            return true;
        }

        if (args[0].equalsIgnoreCase("unblock")) {
            if (args.length < 2) {
                player.sendMessage("§cUsage: /poke unblock <player>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(messageManager.getMessage("poke.player-not-found"));
                return true;
            }
            privacyManager.isBlocked(player.getUniqueId(), target.getUniqueId()).thenAccept(isBlocked -> {
                if (!isBlocked) {
                    player.sendMessage(messageManager.formatMessage("privacy.not-blocked", "target", target.getName()));
                    return;
                }
                privacyManager.unblockPlayer(player.getUniqueId(), target.getUniqueId()).thenRun(() -> {
                    player.sendMessage(messageManager.formatMessage("privacy.unblocked", "target", target.getName()));
                });
            });
            return true;
        }

        if (args[0].equalsIgnoreCase("@a")) {
            if (!player.hasPermission("pokeplugin.admin")) {
                player.sendMessage(messageManager.getMessage("poke.no-permission"));
                return true;
            }
            pokeAll(player);
            return true;
        }

        if (!player.hasPermission("pokeplugin.use")) {
            player.sendMessage(messageManager.getMessage("poke.no-permission"));
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        if (target == null || !target.isOnline()) {
            player.sendMessage(messageManager.getMessage("poke.player-not-found"));
            return true;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(messageManager.getMessage("poke.self-poke"));
            return true;
        }

        if (!player.hasPermission("pokeplugin.bypass.cooldown")) {
            long remainingTime = cooldownManager.getRemainingCooldown(player.getUniqueId());
            if (remainingTime > 0) {
                String timeFormatted = formatTime(remainingTime);
                player.sendMessage(messageManager.formatMessage("poke.cooldown", "time", timeFormatted));
                return true;
            }
        }

        privacyManager.canPoke(player.getUniqueId(), target.getUniqueId()).thenAccept(canPoke -> {
            if (!canPoke) {
                player.sendMessage(messageManager.formatMessage("poke.disabled", "target", target.getName()));
                return;
            }

            privacyManager.isBlocked(target.getUniqueId(), player.getUniqueId()).thenAccept(isBlocked -> {
                if (isBlocked) {
                    player.sendMessage(messageManager.formatMessage("poke.blocked", "target", target.getName()));
                    return;
                }

                if (!player.hasPermission("pokeplugin.bypass.cost")) {
                    double cost = configManager.getConfig().getDouble("poke.cost", 50.0);
                    if (economyManager.isEconomyEnabled()) {
                        if (!economyManager.hasBalance(player, cost)) {
                            player.sendMessage(messageManager.formatMessage("poke.no-balance", "cost", String.valueOf((int) cost)));
                            return;
                        }
                        economyManager.withdraw(player, cost);
                    }
                }

                cooldownManager.setCooldown(player.getUniqueId());
                executePoke(player, target);
            });
        });

        return true;
    }

    private void pokeAll(Player admin) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!online.equals(admin)) {
                executePoke(admin, online);
            }
        }
        admin.sendMessage(messageManager.getMessage("admin.poke-all"));
    }

    private void executePoke(Player poker, Player target) {
        SchedulerUtil.runTask(plugin, target, () -> {
            target.sendMessage(messageManager.formatMessage("poke.received", "player", poker.getName()));

            if (configManager.getConfig().getBoolean("poke.sound", true)) {
                try {
                    String soundType = configManager.getConfig().getString("poke.sound", "ENTITY_EXPERIENCE_ORB_PICKUP");
                    Sound sound = Sound.valueOf(soundType);
                    float volume = (float) configManager.getConfig().getDouble("poke.sound-volume", 1.0);
                    float pitch = (float) configManager.getConfig().getDouble("poke.sound-pitch", 1.0);
                    target.playSound(target.getLocation(), sound, volume, pitch);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid sound type in config");
                }
            }

            if (configManager.getConfig().getBoolean("poke.title.enabled", true)) {
                String mainTitle = configManager.getConfig().getString("poke.title.main", "&aYou've been poked!")
                        .replace("&", "§")
                        .replace("{player}", poker.getName());
                String subtitle = configManager.getConfig().getString("poke.title.subtitle", "&eby {player}")
                        .replace("&", "§")
                        .replace("{player}", poker.getName());

                int fadeIn = configManager.getConfig().getInt("poke.title.fade-in", 10);
                int stay = configManager.getConfig().getInt("poke.title.stay", 40);
                int fadeOut = configManager.getConfig().getInt("poke.title.fade-out", 10);

                Title title = Title.title(
                        Component.text(mainTitle),
                        Component.text(subtitle),
                        Title.Times.times(
                                Duration.ofMillis(fadeIn * 50L),
                                Duration.ofMillis(stay * 50L),
                                Duration.ofMillis(fadeOut * 50L)
                        )
                );
                target.showTitle(title);
            }
        });

        SchedulerUtil.runTask(plugin, poker, () -> {
            poker.sendMessage(messageManager.formatMessage("poke.sent", "target", target.getName()));
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
            completions.add("settings");
            completions.add("allow");
            completions.add("disable");
            completions.add("block");
            completions.add("unblock");

            if (sender.hasPermission("pokeplugin.admin")) {
                completions.add("@a");
            }

            if (sender.hasPermission("pokeplugin.reload")) {
                completions.add("reload");
            }

            completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList()));

            return completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .sorted()
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("block") || args[0].equalsIgnoreCase("unblock"))) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .sorted()
                    .collect(Collectors.toList());
        }

        return completions;
    }
}