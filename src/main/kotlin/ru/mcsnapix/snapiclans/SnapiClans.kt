package ru.mcsnapix.snapiclans

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.milkbowl.vault.economy.Economy
import org.bukkit.plugin.java.JavaPlugin
import ru.mcsnapix.snapiclans.caching.Messenger
import ru.mcsnapix.snapiclans.caching.cache.Caches
import ru.mcsnapix.snapiclans.commands.Commands
import ru.mcsnapix.snapiclans.database.Database
import ru.mcsnapix.snapiclans.managers.Manager
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
        setupEconomy()

        Settings.enable()
        Database.enable()
        Caches.enable()
        Messenger.enable()
        Commands.enable()
    }

    fun reload() {
        Settings.reload()
        Manager.reload()
    }

    override fun onDisable() {
        Messenger.disable()
        Caches.disable()
        Database.disable()
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