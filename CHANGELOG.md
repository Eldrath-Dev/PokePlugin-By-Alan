# Changelog

All notable changes to **PokePlugin** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.0.0] - 2025-10-XX

### 🎉 Initial Release

#### Added
- **Core Poke System**
  - `/poke <player>` command to send poke notifications
  - Player-to-player poke functionality
  - Self-poke prevention
  - Offline player protection
  
- **Cooldown System**
  - 3-hour default cooldown between pokes
  - Configurable cooldown duration (in hours)
  - Persistent cooldown storage (YAML-based)
  - Cooldown survives server restarts
  - Per-player cooldown tracking
  - Bypass permission (`poke.bypass.cooldown`)
  
- **Economy Integration**
  - Vault API integration (soft dependency)
  - Configurable poke cost (default: $50)
  - Balance checking before poke execution
  - Automatic money withdrawal on successful poke
  - Graceful degradation without economy plugin
  - Cost bypass permission (`poke.bypass.cost`)
  
- **Notification System**
  - Chat message notifications
  - Sound effect playback (customizable)
  - Title screen notifications (optional)
  - Configurable notification components
  - Volume and pitch control for sounds
  - Title timing customization (fade-in, stay, fade-out)
  
- **Multi-Platform Support**
  - Bukkit API support (1.20.x - 1.21.x)
  - Spigot API support (1.20.x - 1.21.x)
  - Paper API support (1.20.x - 1.21.x)
  - Purpur API support (1.20.x - 1.21.x)
  - Folia API support (1.20.x - 1.21.x)
  - Automatic platform detection
  - Smart scheduler selection (Folia regionized vs Bukkit standard)
  
- **Configuration System**
  - Comprehensive `config.yml` with all options
  - Customizable messages with color code support
  - Placeholder support (%player%, %target%, %cost%, %time%)
  - `/poke reload` command for hot-reloading
  - Sound type, volume, and pitch configuration
  - Title customization (main, subtitle, timing)
  
- **Permission System**
  - `poke.use` - Base poke permission (default: true)
  - `poke.bypass.cooldown` - Bypass cooldown (default: op)
  - `poke.bypass.cost` - Bypass economy cost (default: op)
  - `poke.reload` - Reload configuration (default: op)
  
- **Data Management**
  - YAML-based cooldown persistence
  - Automatic cooldown cleanup (expired entries)
  - Thread-safe data operations
  - Automatic file creation on first run
  
- **User Experience**
  - Tab completion for player names
  - Tab completion for "reload" subcommand
  - Clear error messages for all failure cases
  - Success confirmation messages
  - Formatted time remaining display (Xh Xm Xs)
  - Colorful console startup banner
  
- **Developer Features**
  - Platform detection logging
  - Economy hook status logging
  - Folia mode indicator
  - Version information display
  - Clean, documented code
  - No deprecated API usage
  - Maven build system
  - Shade plugin configuration

#### Technical Details
- **Language**: Java 21
- **Build Tool**: Maven
- **Dependencies**: 
  - Paper API 1.20.1 (provided)
  - Folia API 1.20.1 (provided)
  - Spigot API 1.20.1 (provided)
  - Purpur API 1.20.1 (provided)
  - Vault API 1.7 (provided)
- **JAR Size**: ~30-50KB
- **API Version**: 1.20
- **Folia Support**: Native (via folia-supported: true)

#### Configuration Options
```yaml
poke-cost: 50                    # Economy cost per poke
cooldown-hours: 3                # Hours between pokes
notifications.title: true        # Enable title notifications
notifications.sound: true        # Enable sound notifications
notifications.sound-type: ...    # Minecraft sound enum
notifications.sound-volume: 1.0  # Sound volume (0.0-1.0)
notifications.sound-pitch: 1.0   # Sound pitch (0.5-2.0)
title.main: "§b§lPOKED!"        # Main title text
title.subtitle: "§eby %player%"  # Subtitle text
title.fade-in: 10                # Fade in ticks
title.stay: 40                   # Stay duration ticks
title.fade-out: 10               # Fade out ticks
