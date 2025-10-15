# PokePlugin - Detailed Description

## 🎯 What is PokePlugin?

PokePlugin is a nostalgic, lightweight Minecraft server plugin that brings back the classic "poke" feature from early social media platforms like Facebook. It allows players to send fun, interactive notifications to each other, fostering community engagement and lighthearted player interaction.

---

## 🌟 Why PokePlugin?

### The Problem
Modern Minecraft servers often lack simple, fun ways for players to interact outside of chat. While there are messaging systems, they can feel formal or intrusive. Server owners need a way to encourage casual, friendly interactions that don't disrupt gameplay.

### The Solution
PokePlugin provides a simple, non-intrusive way for players to get each other's attention. Whether it's saying "hello," checking if a friend is active, or just having fun, pokes add a social layer to your server without the complexity of full social media plugins.

---

## 🎮 How It Works

### Basic Flow
1. **Player initiates**: `/poke PlayerName`
2. **System checks**:
   - Is the target online?
   - Is the poker on cooldown?
   - Does the poker have enough money?
3. **Poke executes**:
   - Target receives chat message
   - Sound effect plays
   - Title screen appears (optional)
   - Money is deducted
   - Cooldown starts
4. **Confirmation**: Poker receives success message

### Smart Prevention System
- **Self-Poke Prevention**: Players can't poke themselves
- **Offline Protection**: Can't poke players who aren't online
- **Cooldown System**: Prevents spam (3 hours default)
- **Economy Balance**: Optional cost creates value and prevents abuse
- **Permission Control**: Granular control over who can poke

---

## 💡 Use Cases

### 1. Community Building
- **Greeting System**: New players can poke staff for help
- **Friend Notifications**: "Hey, I'm online!" without chat spam
- **Event Reminders**: Staff can poke players about upcoming events (with bypass permissions)

### 2. Roleplay Servers
- **Character Interactions**: Non-verbal way to get attention
- **Shop Notifications**: Shop owners can alert customers
- **Quest Systems**: NPCs (staff) can poke players for quests

### 3. Economy Servers
- **Monetization**: Small cost adds to server economy
- **VIP Benefits**: Bypass permissions as a perk
- **Trade Alerts**: Traders can poke for attention

### 4. Survival/SMP Servers
- **Base Visits**: "I'm at your base!" notification
- **Help Requests**: Quick way to get someone's attention
- **Social Features**: Adds personality to player interactions

---

## 🏗️ Technical Architecture

### Platform Detection System
PokePlugin uses intelligent runtime detection to identify the server platform:

```java
Folia Detection → Purpur Detection → Paper Detection → Spigot Detection → Bukkit Fallback