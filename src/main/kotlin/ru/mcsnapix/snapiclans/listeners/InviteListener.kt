package ru.mcsnapix.snapiclans.listeners

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import ru.mcsnapix.snapiclans.Placeholder
import ru.mcsnapix.snapiclans.api.events.ResponseInviteEvent
import ru.mcsnapix.snapiclans.extensions.send
import ru.mcsnapix.snapiclans.managers.invite.InviteStatus
import ru.mcsnapix.snapiclans.settings.Settings

class InviteListener : Listener {
    @EventHandler
    fun onResponseInvite(event: ResponseInviteEvent) {
        val player = Bukkit.getPlayer(event.sender) ?: return

        val config = Settings.message.responseInvite()
        val message = when (event.status) {
            InviteStatus.ACCEPT -> config.accept()
            InviteStatus.DECLINE -> config.decline()
            InviteStatus.IGNORE -> config.ignore()
        }

        player.send(message, Placeholder("receiver", event.receiver), Placeholder("clan", event.clan.name))
    }
}