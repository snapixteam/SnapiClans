package ru.mcsnapix.snapiclansold.api.events.user

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import ru.mcsnapix.snapiclansold.api.clans.Clan
import ru.mcsnapix.snapiclansold.api.clans.ClanUser

class JoinUserEvent(
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