# 🫵 PokePlugin – The Friendly Poke Update

Bring the classic “poke” interaction from early Facebook into Minecraft!  
**PokePlugin** is a fun, lightweight, economy-integrated plugin that lets players poke each other once every 3 hours — for a small in-game cost.

---

## 🎮 Gameplay Overview
Players can use `/poke <player>` to send a playful notification to others.  
The recipient receives an on-screen message and can poke back immediately.  
Each poke costs **$50** (configurable) and has a **3-hour cooldown** to prevent spam.

---

## ⚙️ Plugin Details
- **Name:** PokePlugin  
- **Version:** 1.0  
- **Group ID:** `com.alan`  
- **Artifact ID:** `PokePlugin`  
- **Main Class:** `com.alan.pokePlugin.PokePlugin`  
- **Authors:** `AlanTheDev`  
- **Website:** [https://github.com/Eldrath-Dev](https://github.com/Eldrath-Dev)  
- **Supported Versions:** 1.20.x – 1.21.x  
- **Supported Platforms:** Bukkit, Spigot, Paper, Purpur, Folia  
- **Soft Depend:** Vault (for economy)

---

## 💰 Economy & Cooldowns
- Each poke deducts **$50** from the sender (via Vault).  
- Players can poke again after **3 hours**.  
- Configurable in `config.yml`.

---

## 🔔 Notifications
Players receive:
- A chat message showing who poked them.  
- A title/subtitle pop-up.  
- A sound effect (customizable).

---

## 🧩 Permissions
| Permission | Description | Default |
|-------------|-------------|----------|
| `pokeplugin.use` | Allows use of `/poke` command | true |
| `pokeplugin.admin` | Reload config | op |
| `pokeplugin.bypass.cooldown` | Bypass cooldown restriction | op |

---

## 🧠 Developer Notes
When generating code for this plugin:
- **Always include fully implemented, complete files.**  
- **No TODOs, placeholders, or incomplete stubs.**  
- **Ensure all logic is implemented and functional out-of-the-box.**  
- **Economy integration must gracefully fail if Vault is missing.**

---

### 📦 Ideal Use
Perfect for **social servers**, **roleplay worlds**, or **community-based SMPs**  
where fun interactions matter as much as survival.

---

### 💬 Quote
> “A little poke never hurt anyone — unless you run out of $50.”

---

### 🧾 License
MIT License © 2025 **AlanTheDev**
