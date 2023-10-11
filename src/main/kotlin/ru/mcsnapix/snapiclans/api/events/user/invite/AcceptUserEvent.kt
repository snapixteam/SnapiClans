package ru.mcsnapix.snapiclans.api.events.user.invite

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import ru.mcsnapix.snapiclans.api.clans.Clan
import ru.mcsnapix.snapiclans.api.clans.ClanUser

class AcceptUserEvent(
    val clanUser: ClanUser,
    val clan: Clan
) : Event() {
    companion object {
        val HANDLERS: HandlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }
}