package ru.mcsnapix.snapiclans

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.plugin.java.JavaPlugin
import ru.mcsnapix.snapiclans.database.Databases
import ru.mcsnapix.snapiclans.managers.Manager
import ru.mcsnapix.snapiclans.messenger.Messenger
import ru.mcsnapix.snapiclans.settings.Settings
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

class SnapiClans : JavaPlugin() {
    private var adventure: BukkitAudiences? = null
    lateinit var economy: Economy

    companion object {
        lateinit var instance: SnapiClans
            private set
    }

    override fun onEnable() {
        instance = this
        enable()
    }

    override fun onDisable() {

    }

    fun enable() {
        sendLogoWithMessage(
            "",
            "§fSnapi§4Clans §8(v0.1.0)",
            "§8Made by §oSnapiX Team",
            ""
        )
        setupHook()
        Settings.enable()
        Databases.enable()
        Manager.enable()
        Messenger.enable()
    }

    fun reload() {
        sendLogoWithMessage(
            "",
            "§fSnapi§4Clans §8(v0.1.0)",
            "§8Reload plugin",
            ""
        )
        Settings.reload()
    }

    fun disable() {
        sendLogoWithMessage(
            "",
            "§fSnapi§4Clans §8(v0.1.0)",
            "§8Disable plugin",
            ""
        )
        Messenger.disable()
        Manager.disable()
        Databases.disable()
    }


    private fun setupHook() {
        adventure = BukkitAudiences.create(this)

        sendConsoleMessage("Loading the economy...")
        checkNotNull(server.pluginManager.getPlugin("Vault")) { "Tried to access Vault when the plugin was disabled!" }
        val rsp = server.servicesManager.getRegistration(
            Economy::class.java
        )
        checkNotNull(rsp) { "Tried to access Vault when the plugin was disabled!" }
        economy = rsp.provider
        sendConsoleMessage("successfully loaded economy")

        checkNotNull(server.pluginManager.getPlugin("LastLoginAPI")) { "Tried to access LastLoginAPI when the plugin was disabled!" }
        checkNotNull(server.pluginManager.getPlugin("LuckPerms")) { "Tried to access LuckPerms when the plugin was disabled!" }
    }

    private fun sendConsoleMessage(message: String) {
        server.consoleSender.sendMessage(message)
    }

    private fun sendLogoWithMessage(vararg messages: String) {
        sendConsoleMessage("")
        sendConsoleMessage("§f  ******** §4  ****** ")
        sendConsoleMessage("§f **//////  §4 **////**")
        sendConsoleMessage("§f/**       §4 **    //  ${messages[0]}")
        sendConsoleMessage("§f/*********/§4**        ${messages[1]}")
        sendConsoleMessage("§f////////**/§4**        ${messages[2]}")
        sendConsoleMessage("§f       /**/§4/**    ** ${messages[3]}")
        sendConsoleMessage("§f ******** §4 //****** ")
        sendConsoleMessage("§f////////   §4 //////  ")
        sendConsoleMessage("")
    }

    fun adventure(): BukkitAudiences {
        checkNotNull(adventure) { "Tried to access Adventure when the plugin was disabled!" }
        return adventure as BukkitAudiences
    }

    @OptIn(ExperimentalContracts::class)
    inline fun <T : Any> checkNotNull(value: T?, lazyMessage: () -> Any): T {
        contract {
            returns() implies (value != null)
        }

        if (value == null) {
            val message = lazyMessage()
            Bukkit.getServer().pluginManager.disablePlugin(this)
            throw IllegalStateException(message.toString())
        } else {
            return value
        }
    }
}

fun callEvent(event: Event) {
    Bukkit.getServer().pluginManager.callEvent(event)
}