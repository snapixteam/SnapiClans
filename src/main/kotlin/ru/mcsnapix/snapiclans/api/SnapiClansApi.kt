package ru.mcsnapix.snapiclans.api

import ru.mcsnapix.snapiclans.api.clans.Clan
import ru.mcsnapix.snapiclans.api.clans.ClanUser
import ru.mcsnapix.snapiclans.api.clans.role.ClanRole
import ru.mcsnapix.snapiclans.api.events.clan.CreateClanEvent
import ru.mcsnapix.snapiclans.api.events.user.AddUserEvent
import ru.mcsnapix.snapiclans.database.Database
import ru.mcsnapix.snapiclans.extensions.callEvent
import ru.mcsnapix.snapiclans.registry.ClanUsersRegistry
import ru.mcsnapix.snapiclans.registry.ClansRegistry

object SnapiClansApi {
    fun clan(name: String): Clan? {
        return ClansRegistry.get(name)
    }

    fun createClan(name: String, displayName: String, owner: String) {
        Database.createClan(name, displayName, owner)
        ClansRegistry.add(name)
        ClansRegistry.get(name)?.let { callEvent(CreateClanEvent(it)) }
    }

    fun updateClan(name: String) {
        ClansRegistry.update(name)
    }

    fun updateClan(id: Int) {
        ClansRegistry.name(id)?.let { updateClan(it.name) }
    }

    fun removeClan(name: String) {
        ClansRegistry.get(name)?.let {
            it.members.forEach {user ->
                removeUser(user)
            }
            ClansRegistry.remove(name)
        }
    }

    fun user(name: String): ClanUser? {
        return ClanUsersRegistry.get(name)
    }

    fun createUser(name: String, clan: Clan, role: ClanRole) {
        Database.createUser(clan.id, name, role.name)
        ClanUsersRegistry.add(name)
        updateClan(clan.name)
        ClanUsersRegistry.get(name)?.let { callEvent(AddUserEvent(it, clan)) }
    }

    fun updateUser(name: String) {
        ClanUsersRegistry.update(name)
    }

    fun removeUser(user: ClanUser) {
        ClanUsersRegistry.remove(user.username)
        updateClan(user.clanId)
    }
}