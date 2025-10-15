# 🫵 PokePlugin

[![Paper](https://img.shields.io/badge/Paper-1.20.x--1.21.x-blue.svg)](https://papermc.io)
[![Spigot](https://img.shields.io/badge/Spigot-1.20.x--1.21.x-orange.svg)](https://www.spigotmc.org)
[![Purpur](https://img.shields.io/badge/Purpur-1.20.x--1.21.x-purple.svg)](https://purpurmc.org)
[![Bukkit](https://img.shields.io/badge/Bukkit-1.20.x--1.21.x-red.svg)](https://bukkit.org)
[![Folia](https://img.shields.io/badge/Folia-1.20.x--1.21.x-green.svg)](https://papermc.io/downloads/folia)

---

### 📘 Overview
**PokePlugin** brings the nostalgic Facebook “poke” feature to Minecraft servers!  
Players can poke each other with a simple command — lighthearted, fun, and configurable.  
Built to be **lightweight**, **Folia-safe**, and **Vault-compatible** for economy integration.

---

### ✨ Features
- 🎯 **Simple Command** – `/poke <player>` to send a poke notification.  
- 🔔 **Interactive Notification** – Receiver gets a poke message and can poke back instantly.  
- 💰 **Vault Economy Support** – Costs $50 (configurable) per poke attempt.  
- ⏰ **Anti-Spam System** – 3-hour cooldown between pokes (per player).  
- 🧩 **Multi-Platform Support** – Works with Bukkit, Spigot, Paper, Purpur, and Folia.  
- ⚙️ **Configurable** – Edit costs, cooldowns, messages, and sounds.  
- 💾 **Lightweight & Efficient** – Minimal memory usage and optimized async handling.  

---

### ⚙️ Requirements
- **Java 17 or higher**  
- **Minecraft 1.21.4+**  
- **Vault** (optional for economy features)

---

### 💡 Commands & Permissions
| Command | Description | Permission | Default |
|----------|--------------|------------|----------|
| `/poke <player>` | Poke another player | `pokeplugin.use` | true |
| `/poke reload` | Reload configuration | `pokeplugin.admin` | op |

---

### 💰 Economy Integration
This plugin **hooks into Vault** as a *soft dependency*.  
If Vault or an economy plugin (e.g., EssentialsX Economy) is installed,  
each poke will charge the configured amount (default: `$50`).  
If no economy plugin is found, the command will still work but no money will be charged.

---

### 🧠 Cooldown Logic
- Each player can only poke another once every **3 hours**.  
- The cooldown resets automatically per target.  
- Admins can bypass cooldowns using the permission `pokeplugin.bypass.cooldown`.

---

### 🧩 Configuration Example (`config.yml`)
```yaml
# PokePlugin Configuration

poke-cost: 50
cooldown-hours: 3

messages:
  poke-sent: "&aYou poked &e%target%!"
  poke-received: "&e%player% &ahas poked you! Type &b/poke %player% &ato poke back!"
  cooldown: "&cYou can poke again in %time%."
  insufficient-funds: "&cYou need $%amount% to poke!"
  no-player: "&cPlayer not found!"
  self-poke: "&cYou cannot poke yourself!"

sounds:
  sent: ENTITY_EXPERIENCE_ORB_PICKUP
  received: ENTITY_PLAYER_LEVELUP
