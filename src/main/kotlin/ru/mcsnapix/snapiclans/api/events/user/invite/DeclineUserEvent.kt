package ru.mcsnapix.snapiclans.api.events.user.invite

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import ru.mcsnapix.snapiclans.api.clans.Clan

class DeclineUserEvent(
    val player: Player,
    val clan: Clan
) : Event() {
    companion object {
        val HANDLERS: HandlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }
}