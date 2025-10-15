# 📜 Changelog  
**PokePlugin** — Version 1.0.0  
*Codename: “The Friendly Jab”*  
**Release Date:** 16 October 2025  

---

## 🚀 Version 1.0.0
### Initial Release Highlights
- Added `/poke <player>` command for sending poke notifications.
- Introduced **3-hour cooldown system** to prevent spam.
- Integrated **Vault** for economy transactions (soft-dependency).
- Added **configurable poke cost** (default: $50 per poke).
- Implemented **chat, sound, and title notifications** for poke events.
- Designed **config.yml** with editable messages and options.
- Added **permissions system**:
  - `poke.use` → allows poking other players.
  - `poke.bypass.cooldown` → ignores cooldown.
  - `poke.bypass.cost` → ignores poke cost.
- **Cross-platform compatibility**:
  - Paper, Spigot, Purpur, Bukkit, and Folia (region-thread safe).
- Optimized for **lightweight performance (<100KB)** and **async operations**.

---

## 🧩 Platform Support

[![Paper](https://img.shields.io/badge/Paper-1.20.x--1.21.x-blue.svg)](https://papermc.io)
[![Spigot](https://img.shields.io/badge/Spigot-1.20.x--1.21.x-orange.svg)](https://www.spigotmc.org)
[![Purpur](https://img.shields.io/badge/Purpur-1.20.x--1.21.x-purple.svg)](https://purpurmc.org)
[![Bukkit](https://img.shields.io/badge/Bukkit-1.20.x--1.21.x-red.svg)](https://bukkit.org)
[![Folia](https://img.shields.io/badge/Folia-1.20.x--1.21.x-green.svg)](https://papermc.io/downloads/folia)

---

## 🧠 Developer Notes
- Built with **Java 17** and **Paper 1.21.4 API**.
- Uses **Vault API** (v1.7) for economy hooks.
- Fully compatible with **Folia’s concurrency model**.
- No external dependencies beyond Vault.
