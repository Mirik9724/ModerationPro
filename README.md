## 🔧 **ModerationPro — Discord → Minecraft Moderation Bridge**

**ModerationPro** is a powerful Minecraft plugin that bridges your **Discord server** with your **Spigot server**, allowing authorized staff to **moderate players directly from Discord**.

It is **fully compatible** with the following:

* ✅ **Whitelist Ultra** (for managing whitelists)
* ✅ **AdvancedBan** (for advanced ban and tempban handling)
* ✅ **Discord bots** (using reliable message events)

---

### ✨ **Main Features**

* 🔐 **Role-restricted moderation commands**
  Only users with the specified Discord role can use the bot commands.

* 💬 **Discord Commands**:

    * `!wlu-add @user MinecraftNick` — Adds the player to whitelist and sets their Discord nickname
    * `!wlu-del MinecraftNick` — Removes player from the whitelist
    * `!kick MinecraftNick` — Kicks the player from the server
    * `!ban MinecraftNick Reason` — Permanently bans a player using AdvancedBan
    * `!tempban MinecraftNick Time Reason` — Temporarily bans a player
    * `!unban MinecraftNick` — Unbans a player
    * `!ip` — Sends the server IP (loaded from config)

* 🧠 **Smart Message Responses**
  All reply messages (e.g. success, errors, or info) are fully customizable via a config file.

* 🧵 **Console-level Execution**
  All moderation actions are dispatched as if executed by the Minecraft server console.

* 🔄 **Nickname Sync**
  Automatically updates Discord nicknames to match Minecraft usernames (on whitelist add).

---

### 🛠️ Requirements

* Minecraft server (Spigot or Paper)
* Discord bot token (using [DDP](https://discord.com/developers/applications))
* Installed:

    * ✅ [Whitelist Ultra](https://modrinth.com/plugin/whitelist-ultra)
    * ✅ [AdvancedBan](https://www.spigotmc.org/resources/advancedban.8695/)

---

