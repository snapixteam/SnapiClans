package ru.mcsnapix.snapiclans.listeners

import org.bukkit.Bukkit
import ru.mcsnapix.snapiclans.SnapiClans

object Listeners {
    fun enable() {
        val pm = Bukkit.getServer().pluginManager
        pm.registerEvents(InviteListener(), SnapiClans.instance)
    }
}