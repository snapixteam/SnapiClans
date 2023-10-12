package ru.mcsnapix.snapiclans.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.mcsnapix.snapiclans.registry.ClanUsersRegistry

class PlayerListener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        ClanUsersRegistry[event.player.name]?.let {
            ClanUsersRegistry.add(it.clan.name)
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        ClanUsersRegistry[event.player.name]?.let {
            ClanUsersRegistry.remove(it.clan.name)
        }
    }
}