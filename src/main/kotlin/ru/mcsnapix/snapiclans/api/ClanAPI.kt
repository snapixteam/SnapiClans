package ru.mcsnapix.snapiclans.api

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.Event
import ru.mcsnapix.snapiclans.api.clans.Clan
import ru.mcsnapix.snapiclans.api.clans.ClanRole
import ru.mcsnapix.snapiclans.api.clans.User
import ru.mcsnapix.snapiclans.caching.Messenger
import ru.mcsnapix.snapiclans.caching.actions.*
import ru.mcsnapix.snapiclans.caching.cache.ClanCaches
import ru.mcsnapix.snapiclans.caching.cache.UserCaches
import ru.mcsnapix.snapiclans.database.ClanDatabase
import ru.mcsnapix.snapiclans.database.UserDatabase
import ru.mcsnapix.snapiclans.managers.RoleManager
import ru.mcsnapix.snapiclans.managers.invite.InviteManager
import ru.mcsnapix.snapiclans.settings.Settings
import java.util.*

object ClanAPI {
    /**
     * Calls the specified event.
     *
     * @param event The event to be called.
     */
    @JvmStatic
    fun callEvent(event: Event) {
        Bukkit.getPluginManager().callEvent(event)
    }

    @JvmStatic
    fun clans() = ClanCaches

    @JvmStatic
    fun users() = UserCaches

    /**
     * Creates a new clan.
     *
     * @param name The name of the clan
     * @param displayName The display name of the clan.
     * @param owner The owner of the clan.
     */
    @JvmStatic
    fun createClan(name: String, displayName: String, owner: String) {
        ClanDatabase.add(name, displayName, owner)
        ClanDatabase[name]?.let {
            UserDatabase.add(it.id, owner, RoleManager.owner)
        }
        Messenger.sendOutgoingMessage(CreateClanAction(UUID.randomUUID(), name))
        Messenger.sendOutgoingMessage(CreateUserAction(UUID.randomUUID(), owner))
        Messenger.sendOutgoingMessage(UpdateClanAction(UUID.randomUUID(), name))
    }

    /**
     * Removes the specified clan.
     *
     * @param clan The clan to be removed.
     */
    @JvmStatic
    fun removeClan(clan: Clan) {
        val name = clan.name
        clan.members.forEach {
            UserDatabase.remove(it.name)
            Messenger.sendOutgoingMessage(RemoveUserAction(UUID.randomUUID(), it.name))
        }
        ClanDatabase.remove(name)
        Messenger.sendOutgoingMessage(RemoveClanAction(UUID.randomUUID(), name))
    }

    /**
     * Sends a clan message.
     *
     * @param player The player who sends a message to the clan chat.
     * @param clan The clan in which [User] should receive the message
     * @param message The message to be sent.
     */
    @JvmStatic
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

        player.sendMessage(msg)

        Messenger.sendOutgoingMessage(SendMessageAction(UUID.randomUUID(), sender, nameClan, msg))
    }

    /**
     * Adds a new user to Clan.
     *
     * @param name The name of the creation user
     * @param clan The clan
     * @param role The role of user
     */
    @JvmStatic
    fun createUser(name: String, clan: Clan, role: ClanRole) {
        UserDatabase.add(clan.id, name, role)
        Messenger.sendOutgoingMessage(CreateUserAction(UUID.randomUUID(), name))
        Messenger.sendOutgoingMessage(UpdateClanAction(UUID.randomUUID(), clan.name))
    }

    /**
     * Remove a user from Clan.
     *
     * @param name The name of the user
     */
    @JvmStatic
    fun removeUser(name: String) {
        val user = UserCaches[name] ?: return
        UserDatabase.remove(name)
        InviteManager.remove(name)
        Messenger.sendOutgoingMessage(CreateUserAction(UUID.randomUUID(), name))
        Messenger.sendOutgoingMessage(UpdateClanAction(UUID.randomUUID(), user.clan.name))
    }

    @JvmStatic
    fun getUserClan(name: String): Clan? {
        val user = UserCaches[name] ?: return null
        return user.clan
    }
}