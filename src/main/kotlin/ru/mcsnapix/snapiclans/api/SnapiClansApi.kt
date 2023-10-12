package ru.mcsnapix.snapiclans.api

import ru.mcsnapix.snapiclans.api.clans.Clan
import ru.mcsnapix.snapiclans.api.clans.ClanUser
import ru.mcsnapix.snapiclans.api.clans.role.ClanRole
import ru.mcsnapix.snapiclans.api.events.clan.CreateClanEvent
import ru.mcsnapix.snapiclans.api.events.clan.RemoveClanEvent
import ru.mcsnapix.snapiclans.api.events.user.JoinUserEvent
import ru.mcsnapix.snapiclans.api.events.user.KickUserEvent
import ru.mcsnapix.snapiclans.api.events.user.LeaveUserEvent
import ru.mcsnapix.snapiclans.database.Database
import ru.mcsnapix.snapiclans.extensions.callEvent
import ru.mcsnapix.snapiclans.messenger.SQLMessenger
import ru.mcsnapix.snapiclans.messenger.message.ClanUpdateMessage
import ru.mcsnapix.snapiclans.messenger.message.UserUpdateMessage
import ru.mcsnapix.snapiclans.registry.ClanUsersRegistry
import ru.mcsnapix.snapiclans.registry.ClansRegistry
import ru.mcsnapix.snapiclans.registry.invite.InvitationRegistry
import java.util.*

object SnapiClansApi {
    fun clan(name: String): Clan? = ClansRegistry[name]
    fun clan(id: Int): Clan? = ClansRegistry[id]

    fun clans() = ClansRegistry.list()

    fun createClan(name: String, displayName: String, owner: String) {
        Database.createClan(name, displayName, owner)
        ClansRegistry.add(name)
        SQLMessenger.sendOutgoingMessage(ClanUpdateMessage(UUID.randomUUID(), name))
        ClansRegistry[name]?.let { callEvent(CreateClanEvent(it)) }
    }

    fun changeClanName(name: String, displayName: String) {
        Database.changeClanDisplayName(name, displayName)
        SQLMessenger.sendOutgoingMessage(ClanUpdateMessage(UUID.randomUUID(), name))
    }

    fun updateClan(name: String) {
        ClansRegistry.update(name)
    }

    fun updateClan(clan: Clan?) {
        clan?.let { updateClan(it.name) }
    }

    fun removeClan(name: String) {
        ClansRegistry[name]?.let {
            it.members.forEach { user ->
                callEvent(LeaveUserEvent(user, user.clan))
                removeUser(user)
            }

            ClansRegistry.remove(name)
            Database.removeClan(it.id)
            SQLMessenger.sendOutgoingMessage(ClanUpdateMessage(UUID.randomUUID(), name))
            callEvent(RemoveClanEvent(it))
        }
    }

    fun user(name: String): ClanUser? {
        return ClanUsersRegistry[name]
    }

    fun createUser(name: String, clan: Clan, role: ClanRole) {
        Database.createUser(clan.id, name, role.name)
        ClanUsersRegistry.add(name)
        SQLMessenger.sendOutgoingMessage(UserUpdateMessage(UUID.randomUUID(), name))
        SQLMessenger.sendOutgoingMessage(ClanUpdateMessage(UUID.randomUUID(), clan.name))
        ClanUsersRegistry[name]?.let { callEvent(JoinUserEvent(it, clan)) }
    }

    fun updateUser(name: String) {
        ClanUsersRegistry.update(name)
    }

    fun removeUser(user: ClanUser) {
        ClanUsersRegistry.remove(user.username)
        Database.removeUser(user.username)
        SQLMessenger.sendOutgoingMessage(UserUpdateMessage(UUID.randomUUID(), user.username))
        SQLMessenger.sendOutgoingMessage(ClanUpdateMessage(UUID.randomUUID(), user.clan.name))
    }

    fun leaveUser(user: ClanUser) {
        ClanUsersRegistry[user.username]?.let { callEvent(LeaveUserEvent(it, user.clan)) }
        removeUser(user)
    }

    fun kickUser(user: ClanUser) {
        ClanUsersRegistry[user.username]?.let { callEvent(KickUserEvent(it, user.clan)) }
        removeUser(user)
    }
}