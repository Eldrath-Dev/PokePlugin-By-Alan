# 🫵 PokePlugin

<div align="center">

![PokePlugin Banner](https://img.shields.io/badge/PokePlugin-v1.0.0-blue?style=for-the-badge)
![Minecraft](https://img.shields.io/badge/Minecraft-1.20.x--1.21.x-green?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

**Bring back the nostalgic "poke" feature to your Minecraft server!**

[Features](#features) • [Installation](#installation) • [Commands](#commands) • [Configuration](#configuration) • [Support](#support)

</div>

---

## 📖 Overview

**PokePlugin** brings the classic social "poke" feature from early Facebook to Minecraft! Players can send fun notifications to each other, encouraging lighthearted interaction on your server. With built-in cooldowns and economy integration, PokePlugin maintains balance while adding a nostalgic touch to player communication.

---

## ✨ Features

- 🎯 **Simple Poke Command** - `/poke <player>` to send notifications
- ⏰ **Cooldown System** - 3-hour cooldown prevents spam (configurable)
- 💰 **Economy Integration** - Vault support for poke costs (optional)
- 🔔 **Rich Notifications** - Chat messages, sounds, and title screens
- 🛡️ **Permission System** - Full control over who can poke and bypass restrictions
- 🌐 **Multi-Platform Support** - Works on Bukkit, Spigot, Paper, Purpur, and Folia
- 📦 **Lightweight** - <50KB JAR with minimal performance impact
- ⚙️ **Highly Configurable** - Customize messages, costs, cooldowns, and notifications
- 💾 **Persistent Storage** - Cooldowns saved between server restarts
- 🔄 **Hot Reload** - `/poke reload` to update config without restart

---

## 🎮 Platform Compatibility

| Platform | Version Support | Status |
|----------|----------------|--------|
| **Bukkit** | 1.20.x - 1.21.x | ✅ Fully Supported |
| **Spigot** | 1.20.x - 1.21.x | ✅ Fully Supported |
| **Paper** | 1.20.x - 1.21.x | ✅ Fully Supported |
| **Purpur** | 1.20.x - 1.21.x | ✅ Fully Supported |
| **Folia** | 1.20.x - 1.21.x | ✅ Fully Supported |

---

## 📥 Installation

### Prerequisites
- Minecraft server running version **1.20.x - 1.21.x**
- Java 21 or higher
- *Optional:* [Vault](https://www.spigotmc.org/resources/vault.34315/) for economy features
- *Optional:* Economy plugin (EssentialsX, CMI, etc.)

### Steps
1. Download the latest `PokePlugin-X.X.X.jar` from [Releases](https://github.com/Eldrath-Dev/PokePlugin/releases)
2. Place the JAR file in your server's `plugins/` folder
3. *(Optional)* Install Vault and an economy plugin
4. Restart your server
5. Configure the plugin in `plugins/PokePlugin/config.yml`
6. Reload with `/poke reload` or restart again

---

## 🎯 Commands

| Command | Description | Permission | Aliases |
|---------|-------------|------------|---------|
| `/poke <player>` | Send a poke to another player | `poke.use` | None |
| `/poke reload` | Reload the plugin configuration | `poke.reload` | None |

---

## 🔑 Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `poke.use` | Allows using the `/poke` command | `true` |
| `poke.bypass.cooldown` | Bypass the poke cooldown | `op` |
| `poke.bypass.cost` | Bypass the poke cost | `op` |
| `poke.reload` | Allows reloading the configuration | `op` |

---

## ⚙️ Configuration

### Default `config.yml`

```yaml
# PokePlugin Configuration

# Cost in server currency to poke someone
poke-cost: 50

# Cooldown in hours between pokes
cooldown-hours: 3

# Message Configuration
messages:
  no-balance: "§cYou need $%cost% to poke someone!"
  cooldown: "§eYou must wait §c%time% §ebefore poking again!"
  poked: "§bYou've been poked by §e%player%§b!"
  poke-success: "§aYou poked §e%target% §asuccessfully!"
  player-not-found: "§cThat player is not online!"
  self-poke: "§cYou can't poke yourself!"
  no-economy: "§cEconomy system not found! Pokes are free."
  reload-success: "§aPokePlugin configuration reloaded successfully!"
  no-permission: "§cYou don't have permission to use this command!"

# Notification Settings
notifications:
  title: true
  sound: true
  sound-type: ENTITY_EXPERIENCE_ORB_PICKUP
  sound-volume: 1.0
  sound-pitch: 1.0

# Title Configuration
title:
  main: "§b§lPOKED!"
  subtitle: "§eby %player%"
  fade-in: 10
  stay: 40
  fade-out: 10