package ru.mcsnapix.snapiclans

import org.bukkit.plugin.java.JavaPlugin
import ru.mcsnapix.snapiclans.commands.Commands
import ru.mcsnapix.snapiclans.database.Database
import ru.mcsnapix.snapiclans.listeners.Listeners
import ru.mcsnapix.snapiclans.registry.Registry
import ru.mcsnapix.snapiclans.settings.Settings

class SnapiClans : JavaPlugin() {
    companion object {
        @JvmStatic
        lateinit var instance: SnapiClans
            private set
    }

    override fun onEnable() {
        instance = this

        Settings.enable()
        Database.enable()
        Listeners.enable()
        Commands.enable()
        Registry.enable()
    }

    fun reload() {
        Settings.reload()
        Database.reload()
        Registry.reload()
    }

    override fun onDisable() {
        Database.disable()
        Registry.disable()
    }
}