package ru.mcsnapix.snapiclans.managers

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import ru.mcsnapix.snapiclans.SnapiClans
import ru.mcsnapix.snapiclans.api.Clan
import ru.mcsnapix.snapiclans.api.ClansAPI
import ru.mcsnapix.snapiclans.api.roles.ClanRole
import ru.mcsnapix.snapiclans.database.*
import ru.mcsnapix.snapiclans.messenger.Messenger
import ru.mcsnapix.snapiclans.messenger.actions.ResponseInviteAction
import ru.mcsnapix.snapiclans.send
import ru.mcsnapix.snapiclans.settings.Settings
import java.sql.ResultSet
import java.sql.Timestamp
import java.util.*

object InviteManager {
    private var pollTask: BukkitTask? = null

    fun enable() {
        pollTask = Bukkit.getScheduler().runTaskTimerAsynchronously(SnapiClans.instance, { pollMessages() }, 0L, 60L)
    }

    fun disable() {
        pollTask?.cancel()
        pollTask = null
    }

    private fun pollMessages() {
        val list = executeQuery("SELECT * FROM `clan_invite`") { rs ->
            rs
        }.forEach {
            val id = it.getInt("id")
            val clanId = it.getInt("clan_id")
            val sender = it.getString("sender")
            val receiver = it.getString("receiver")
            val time = it.getTimestamp("time")
            if (time.time >= Settings.config.inviteReplySeconds() * 1000 + System.currentTimeMillis()) {
                ignore(sender, receiver)
            }
        }
    }

    fun add(clan: Clan, sender: String, receiver: String) {
        executeUpdate("INSERT INTO `clan_invite`(`clan_id`, `sender`, `receiver`) VALUES ('${clan.id}', '$sender', '$receiver')")
    }

    fun get(sender: String, receiver: String): ResultSet? {
        var result: ResultSet? = null
        executeQuery("SELECT * FROM `clan_invite` WHERE `sender` = '$sender' AND `receiver` = '$receiver'") {
            result = it
        }
        return result
    }

    fun accept(sender: String, receiver: Player) {
        val clan = ClansAPI.getUserClan(sender)
        val clanInDatabase = get(sender, receiver.name)

        remove(sender, receiver.name)

        if (clan == null || clanInDatabase == null || ClanCache.get { it.id == clanInDatabase.getInt("clan_id") } != clan) {
            receiver.send(Settings.message.commands().accept().error())
            return
        }

        runBlocking {
            val result = async {
                UserService.create(ExposedUser(clan.id, receiver.name, ClanRole.default()))
            }
            result.await()
        }

        receiver.send(Settings.message.commands().accept().success())
        Messenger.sendOutgoingMessage(
            ResponseInviteAction(
                UUID.randomUUID(),
                sender,
                receiver.name,
                clan.name,
                InviteStatus.ACCEPT
            )
        )
    }

    fun decline(sender: String, receiver: Player) {
        val clan = ClansAPI.getUserClan(sender)
        val clanInDatabase = get(sender, receiver.name)

        remove(sender, receiver.name)

        if (clan == null || clanInDatabase == null || ClanCache.get { it.id == clanInDatabase.getInt("clan_id") } != clan) {
            receiver.send(Settings.message.commands().decline().error())
            return
        }

        receiver.send(Settings.message.commands().decline().success())
        Messenger.sendOutgoingMessage(
            ResponseInviteAction(
                UUID.randomUUID(),
                sender,
                receiver.name,
                clan.name,
                InviteStatus.DECLINE
            )
        )
    }

    fun ignore(sender: String, receiver: String) {
        val clan = ClansAPI.getUserClan(sender) ?: return

        remove(sender, receiver)
        Messenger.sendOutgoingMessage(
            ResponseInviteAction(
                UUID.randomUUID(),
                sender,
                receiver,
                clan.name,
                InviteStatus.IGNORE
            )
        )
    }

    fun remove(sender: String, receiver: String) {
        executeUpdate("DELETE FROM `clan_invite` WHERE `sender` = '$sender' AND `receiver` = '$receiver'")
    }

    fun remove(sender: String) {
        executeUpdate("DELETE FROM `clan_invite` WHERE `sender` = '$sender'")
    }

    fun remove(id: Int) {
        executeUpdate("DELETE FROM `clan_invite` WHERE `id` = '$id'")
    }
}

enum class InviteStatus {
    ACCEPT,
    DECLINE,
    IGNORE
}