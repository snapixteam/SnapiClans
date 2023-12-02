package ru.mcsnapix.snapiclans.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import ru.mcsnapix.snapiclans.api.events.SendChatMessageEvent

class ChatListener : Listener {
    @EventHandler
    fun onSendChatMessage(event: SendChatMessageEvent) {

    }
}