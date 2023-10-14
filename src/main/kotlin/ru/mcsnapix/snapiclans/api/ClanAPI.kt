package ru.mcsnapix.snapiclans.api

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.Event
import ru.mcsnapix.snapiclans.api.clans.Clan
import ru.mcsnapix.snapiclans.caching.Messenger
import ru.mcsnapix.snapiclans.caching.actions.*
import ru.mcsnapix.snapiclans.caching.cache.ClanCaches
import ru.mcsnapix.snapiclans.caching.cache.UserCaches
import ru.mcsnapix.snapiclans.database.ClanDatabase
import ru.mcsnapix.snapiclans.database.UserDatabase
import ru.mcsnapix.snapiclans.extensions.send
import ru.mcsnapix.snapiclans.managers.RoleManager
import ru.mcsnapix.snapiclans.settings.Settings
import java.util.*

object ClanAPI {
    fun callEvent(event: Event) {
        Bukkit.getPluginManager().callEvent(event)
    }

    fun clans() = ClanCaches
    fun users() = UserCaches

    fun createClan(name: String, displayName: String, owner: String) {
        ClanDatabase.add(name, displayName, owner)
        ClanDatabase[name]?.let {
            UserDatabase.add(it.id, owner, RoleManager.owner)
        }
        Messenger.sendOutgoingMessage(CreateClanAction(UUID.randomUUID(), name))
        Messenger.sendOutgoingMessage(CreateUserAction(UUID.randomUUID(), owner))
        Messenger.sendOutgoingMessage(UpdateClanAction(UUID.randomUUID(), name))
    }

    fun removeClan(clan: Clan) {
        val name = clan.name
        clan.members.forEach {
            UserDatabase.remove(it.name)
            Messenger.sendOutgoingMessage(RemoveUserAction(UUID.randomUUID(), it.name))
        }
        ClanDatabase.remove(name)
        Messenger.sendOutgoingMessage(RemoveClanAction(UUID.randomUUID(), name))
    }

    fun sendMessage(player: Player, clan: Clan, message: String) {
        var msg = if (!player.hasPermission("snapiclans.chat.color")) "&([A-z0-9])".toRegex()
            .replace(message, "") else message

        val sender = player.name
        val nameClan = clan.name

        msg = ChatColor.translateAlternateColorCodes(
            '&',
            Settings.config.chatFormat()
                .replace("%sender%", sender)
                .replace("%message%", msg)
        )

        player.send(msg)

        Messenger.sendOutgoingMessage(SendMessageAction(UUID.randomUUID(), sender, nameClan, msg))
    }
}