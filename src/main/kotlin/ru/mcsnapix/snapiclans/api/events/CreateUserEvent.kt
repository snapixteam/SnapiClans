package ru.mcsnapix.snapiclans.api.events

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import ru.mcsnapix.snapiclans.api.clans.User

class CreateUserEvent(val user: User) : Event() {
    companion object {
        val HANDLERS: HandlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }
}