package ru.mcsnapix.snapiclans.api.events

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import ru.mcsnapix.snapiclans.api.Clan

/**
 * @author Flaimer
 * @since 0.0.3
 */
class SendChatMessageEvent(val clan: Clan, val user: User, val message: String) : Event() {
    companion object {
        val HANDLERS: HandlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }
}