package ru.mcsnapix.snapiclans.api

import org.bukkit.Bukkit
import org.bukkit.event.Event
import ru.mcsnapix.snapiclans.api.clans.Clan
import ru.mcsnapix.snapiclans.caching.Messenger
import ru.mcsnapix.snapiclans.caching.actions.CreateClanAction
import ru.mcsnapix.snapiclans.caching.actions.CreateUserAction
import ru.mcsnapix.snapiclans.caching.cache.ClanCaches
import ru.mcsnapix.snapiclans.caching.cache.UserCaches
import ru.mcsnapix.snapiclans.database.ClanDatabase
import ru.mcsnapix.snapiclans.database.UserDatabase
import ru.mcsnapix.snapiclans.managers.RoleManager
import java.util.*

object ClanAPI {
    fun callEvent(event: Event) {
        Bukkit.getPluginManager().callEvent(event)
    }

    fun clanCaches() = ClanCaches
    fun userCaches() = UserCaches

    fun createClan(name: String, displayName: String, owner: String) {
        ClanDatabase.add(name, displayName, owner)
        ClanDatabase[name]?.let {
            UserDatabase.add(it.id, owner, RoleManager.owner)
        }
        Messenger.sendOutgoingMessage(CreateClanAction(UUID.randomUUID(), name))
        Messenger.sendOutgoingMessage(CreateUserAction(UUID.randomUUID(), name))
    }

    fun removeClan(clan: Clan) {
        val name = clan.name
        clan.members.forEach {
            UserDatabase.remove(it.name)
        }
        ClanDatabase.remove(name)

    }
}