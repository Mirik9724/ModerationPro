package net.Mirik9724.moderationPro

import net.Mirik9724.api.*
import net.Mirik9724.moderationPro.ModerationPro.Companion.pcon
import net.Mirik9724.moderationPro.ModerationPro.Companion.plugin2
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import org.bukkit.Bukkit

object DiscordBot : ListenerAdapter() {

    lateinit var token: String
    val requiredRoleId = Config.getData(pcon, "role_id")

    fun start(token: String) {
        this.token = token

        val jda = JDABuilder.createDefault(token)
            .addEventListeners(this)
            .enableIntents(GatewayIntent.MESSAGE_CONTENT)
            .build()

        logger_.info("Bot started")
    }

    private fun hasRole(member: net.dv8tion.jda.api.entities.Member?, roleId: String?): Boolean {
        if (member == null || roleId == null) return false
        return member.roles.any { it.id == roleId }
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return

        val message = event.message.contentRaw
        val member = event.member ?: return

        if (!hasRole(member, requiredRoleId)) return

        when {
            message.startsWith("!wlu-add") -> {
                val args = message.removePrefix("!wlu-add").trim()
                val parts = args.split(" ", limit = 2)
                val mentionedMembers = event.message.mentions.members

                if (parts.size == 2 && mentionedMembers.isNotEmpty()) {
                    val memberToRename = mentionedMembers[0]
                    val mcNick = parts[1].trim()

                    // Меняем ник в Discord
                    memberToRename.modifyNickname(mcNick).queue()

                    // Подготавливаем сообщение об успешном добавлении
                    val response = Config.getData(pcon, "inf.wlu+").replace("@nick", mcNick)

                    // Выполняем добавление в вайтлист в основном потоке Bukkit
                    Bukkit.getScheduler().runTask(plugin2, Runnable {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wlu add $mcNick")
                    })

                    // Отправляем сообщение в Discord
                    event.channel.sendMessage(response).queue()
                    logger_.info(response)

                } else {
                    // Недостаточно аргументов
                    event.channel.sendMessage(Config.getData(pcon, "inf.subcom")).queue()
                }
            }


            message.startsWith("!wlu-del") -> {
                val args = message.removePrefix("!wlu-del").trim()

                if (args.isNotEmpty()) {
                    val mcNick = args

                    // Подготавливаем сообщение об удалении
                    val response = Config.getData(pcon, "inf.wlu-").replace("@nick", mcNick)

                    // Выполняем удаление из вайтлиста в основном потоке Bukkit
                    Bukkit.getScheduler().runTask(plugin2, Runnable {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wlu remove $mcNick")
                    })

                    // Пробуем найти пользователя по Discord ID или нику
                    val member = event.guild.getMembers().find {
                        it.nickname?.equals(mcNick, ignoreCase = true) == true ||
                                it.user.name.equals(mcNick, ignoreCase = true)
                    }

                    // Если нашли — сбрасываем никнейм
                    member?.modifyNickname(null)?.queue()

                    // Отправляем сообщение в Discord
                    event.channel.sendMessage(response).queue()
                    logger_.info(response)
                } else {
                    event.channel.sendMessage(Config.getData(pcon, "inf.subcom")).queue()
                }
            }

            message.startsWith("!wlu-change") -> {
                val args = message.removePrefix("!wlu-change").trim()
                val parts = args.split(" ", limit = 2)
                val mentionedMembers = event.message.mentions.members

                if (parts.size == 2 && mentionedMembers.isNotEmpty()) {
                    val oldNick = parts[0].trim('@', ' ')
                    val newNick = parts[1].trim()
                    val memberToUpdate = mentionedMembers[0]

                    // Меняем ник в Discord
                    memberToUpdate.modifyNickname(newNick).queue()

                    // Удаляем старый ник из вайтлиста
                    Bukkit.getScheduler().runTask(plugin2, Runnable {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wlu remove $oldNick")
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wlu add $newNick")
                    })

                    val response = Config.getData(pcon, "inf.change")
                        .replace("@old", oldNick)
                        .replace("@new", newNick)

                    event.channel.sendMessage(response).queue()
                    logger_.info("Пользователь $oldNick изменён на $newNick и добавлен в вайтлист.")
                } else {
                    event.channel.sendMessage(Config.getData(pcon, "inf.subcom")).queue()
                }
            }


            message.startsWith("!kick") -> {
                val args = message.removePrefix("!kick").trim()

                if (args.isNotEmpty()) {
                    val playerName = args

                    // Подготавливаем сообщение об кике
                    val response = Config.getData(pcon, "inf.kick").replace("@nick", playerName)

                    // Выполняем команду кика в основном потоке Bukkit
                    Bukkit.getScheduler().runTask(plugin2, Runnable {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick $playerName")
                    })

                    // Отправляем сообщение в Discord
                    event.channel.sendMessage(response).queue()
                    logger_.info(response)
                } else {
                    event.channel.sendMessage(Config.getData(pcon, "inf.subcom")).queue()
                }
            }

            message.startsWith("!ip") -> {
                val ipRoleId = Config.getData(pcon, "ip_role_id")

                if (!hasRole(member, ipRoleId)) {
                    val deniedMessage = Config.getData(pcon, "ip_denied")
                    event.channel.sendMessage(deniedMessage).queue()
                    return
                }

                val response = Config.getData(pcon, "inf.ip")
                event.channel.sendMessage(response).queue()
            }

            message.startsWith("!ban") -> {
                val args = message.removePrefix("!ban").trim().split(" ", limit = 2)
                if (args.size == 2) {
                    val mcNick = args[0]
                    val reason = args[1]
                    val staff = event.member?.effectiveName ?: "Unknown"

                    Bukkit.getScheduler().runTask(plugin2, Runnable {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban $mcNick $reason")
                    })

                    val response = Config.getData(pcon, "inf.ban")
                        .replace("@nick", mcNick)
                        .replace("@reason", reason)
                        .replace("@staff", staff)

                    event.channel.sendMessage(response).queue()
                    logger_.info("$mcNick was permanently banned by $staff for: $reason")
                } else {
                    event.channel.sendMessage(Config.getData(pcon, "inf.subcom")).queue()
                }
            }

            message.startsWith("!tempban") -> {
                val args = message.removePrefix("!tempban").trim().split(" ", limit = 3)
                if (args.size == 3) {
                    val mcNick = args[0]
                    val time = args[1]
                    val reason = args[2]
                    val staff = event.member?.effectiveName ?: "Unknown"

                    Bukkit.getScheduler().runTask(plugin2, Runnable {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tempban $mcNick $time $reason")
                    })

                    val response = Config.getData(pcon, "inf.tempban")
                        .replace("@nick", mcNick)
                        .replace("@time", time)
                        .replace("@reason", reason)
                        .replace("@staff", staff)

                    event.channel.sendMessage(response).queue()
                    logger_.info("$mcNick was temporarily banned for $time by $staff for: $reason")
                } else {
                    event.channel.sendMessage(Config.getData(pcon, "inf.subcom")).queue()
                }
            }

            message.startsWith("!unban") -> {
                val mcNick = message.removePrefix("!unban").trim()
                if (mcNick.isNotEmpty()) {
                    val staff = event.member?.effectiveName ?: "Unknown"

                    Bukkit.getScheduler().runTask(plugin2, Runnable {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "unban $mcNick")
                    })

                    val response = Config.getData(pcon, "inf.unban")
                        .replace("@nick", mcNick)
                        .replace("@staff", staff)

                    event.channel.sendMessage(response).queue()
                    logger_.info("$mcNick was unbanned by $staff")
                } else {
                    event.channel.sendMessage(Config.getData(pcon, "inf.subcom")).queue()
                }
            }

            message.startsWith("!help") -> {
                val helpMessage = """
        **Available commands:**
        `!wlu-add @user <mcNick>` - Add user to whitelist and change Discord nickname
        `!wlu-del <mcNick>` - Remove user from whitelist and reset Discord nickname
        `!wlu-change @user <newMcNick>` - Change Minecraft nickname and Discord nickname
        `!kick <player>` - Kick a player from the server
        `!ban <player> <reason>` - Ban a player with reason
        `!tempban <player> <time> <reason>` - Temporarily ban a player
        `!unban <player>` - Unban a player
        `!ip` - Show server IP address
        `!help` - Show this help message
    """.trimIndent()

                event.channel.sendMessage(helpMessage).queue()
            }


        }
    }
}
