package net.Mirik9724.moderationPro

import net.Mirik9724.api.*
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class ModerationPro : JavaPlugin() {
    companion object {
        lateinit var plugin2: JavaPlugin
        val path = File("plugins/ModerationPro")
        const val conf = "config.yml"
        val pcon = File(path, conf)
    }

    override fun onEnable() {
        plugin2 = this
        LogInit("ModerationPro")
        Config.cloneConfigFromJar(conf, path)

        DiscordBot.start(Config.getData(pcon, "ds_token"))
        logger_.info("ON")
//        logger_.info(Config.getData(pcon, "commmands.kick")) //[!kick]
    }

    override fun onDisable() {
        logger_.info("OFF")
    }
}
