package ru.mcsnapix.snapiclans.api.events

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import ru.mcsnapix.snapiclans.api.Clan

/**
 * @author Flaimer
 * @since 0.0.3
 */
class ResponseInviteEvent(val clan: Clan, val sender: String, val receiver: String, val status: InviteStatus) :
    Event() {
    companion object {
        val HANDLERS: HandlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }
}