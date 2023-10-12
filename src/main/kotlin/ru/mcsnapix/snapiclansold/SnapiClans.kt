package ru.mcsnapix.snapiclansold

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.milkbowl.vault.economy.Economy
import org.bukkit.plugin.java.JavaPlugin
import ru.mcsnapix.snapiclansold.commands.Commands
import ru.mcsnapix.snapiclansold.database.Database
import ru.mcsnapix.snapiclansold.listeners.Listeners
import ru.mcsnapix.snapiclansold.messenger.SQLMessenger
import ru.mcsnapix.snapiclansold.registry.Registry
import ru.mcsnapix.snapiclans.settings.Settings


class SnapiClans : JavaPlugin() {
    private var adventure: BukkitAudiences? = null
    lateinit var economy: Economy

    companion object {
        @JvmStatic
        lateinit var instance: SnapiClans
            private set
    }

    override fun onEnable() {
        instance = this
        adventure = BukkitAudiences.create(this)

        Settings.enable()
        Database.enable()
        Listeners.enable()
        Commands.enable()
        Registry.enable()
        SQLMessenger.enable()
        setupEconomy()
    }

    fun reload() {
        Settings.reload()
        Registry.reload()
    }

    override fun onDisable() {
        Database.disable()
        Registry.disable()
        SQLMessenger.disable()
        adventure?.let {
            adventure!!.close()
            adventure = null
        }
    }

    private fun setupEconomy() {
        if (server.pluginManager.getPlugin("Vault") == null) {
            return
        }
        val rsp = server.servicesManager.getRegistration(
            Economy::class.java
        ) ?: return
        economy = rsp.provider
    }

    fun adventure(): BukkitAudiences {
        checkNotNull(adventure) { "Tried to access Adventure when the plugin was disabled!" }
        return adventure!!
    }

}