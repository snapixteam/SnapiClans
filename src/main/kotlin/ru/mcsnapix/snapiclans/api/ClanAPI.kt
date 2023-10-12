package ru.mcsnapix.snapiclans.api

import org.bukkit.Bukkit
import org.bukkit.event.Event

object ClanAPI {
    fun callEvent(event: Event) {
        Bukkit.getPluginManager().callEvent(event)
    }
}