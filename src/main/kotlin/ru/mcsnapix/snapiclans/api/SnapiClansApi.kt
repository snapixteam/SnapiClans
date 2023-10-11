package ru.mcsnapix.snapiclans.api

import ru.mcsnapix.snapiclans.api.clans.Clan
import ru.mcsnapix.snapiclans.api.clans.ClanUser
import ru.mcsnapix.snapiclans.api.clans.role.ClanRole
import ru.mcsnapix.snapiclans.api.events.clan.CreateClanEvent
import ru.mcsnapix.snapiclans.api.events.user.JoinUserEvent
import ru.mcsnapix.snapiclans.database.Database
import ru.mcsnapix.snapiclans.extensions.callEvent
import ru.mcsnapix.snapiclans.registry.ClanUsersRegistry
import ru.mcsnapix.snapiclans.registry.ClansRegistry

object SnapiClansApi {
    fun clan(name: String): Clan? = ClansRegistry.get(name)
    fun clan(id: Int): Clan? = ClansRegistry.name(id)

    fun createClan(name: String, displayName: String, owner: String) {
        Database.createClan(name, displayName, owner)
        ClansRegistry.add(name)
        ClansRegistry.get(name)?.let { callEvent(CreateClanEvent(it)) }
    }

    fun updateClan(name: String) {
        ClansRegistry.update(name)
    }

    fun updateClan(clan: Clan?) {
        clan?.let { ClansRegistry.update(it.name) }
    }

    fun removeClan(name: String) {
        ClansRegistry.get(name)?.let {
            it.members.forEach { user ->
                removeUser(user)
            }
            ClansRegistry.remove(name)
            Database.removeClan(it.id)
        }
    }

    fun user(name: String): ClanUser? {
        return ClanUsersRegistry.get(name)
    }

    fun createUser(name: String, clan: Clan, role: ClanRole) {
        Database.createUser(clan.id, name, role.name)
        ClanUsersRegistry.add(name)
        updateClan(clan)
        ClanUsersRegistry.get(name)?.let { callEvent(JoinUserEvent(it, clan)) }
    }

    fun updateUser(name: String) {
        ClanUsersRegistry.update(name)
    }

    fun removeUser(user: ClanUser) {
        ClanUsersRegistry.remove(user.username)
        updateClan(user.clan)
        Database.removeUser(user.username)
    }
}