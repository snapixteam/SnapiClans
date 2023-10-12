package ru.mcsnapix.snapiclansold.api.events.user.invite

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import ru.mcsnapix.snapiclansold.api.clans.Clan

class InviteUserEvent(
    val inviter: String,
    val invited: String,
    val clan: Clan
) : Event() {
    companion object {
        val HANDLERS: HandlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }
}