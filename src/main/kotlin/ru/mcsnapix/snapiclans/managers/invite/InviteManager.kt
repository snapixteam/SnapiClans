package ru.mcsnapix.snapiclans.managers.invite

import co.aikar.idb.DB
import co.aikar.idb.DbRow
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import ru.mcsnapix.snapiclans.SnapiClans
import ru.mcsnapix.snapiclans.api.ClanAPI
import ru.mcsnapix.snapiclans.api.clans.Clan
import ru.mcsnapix.snapiclans.caching.Messenger
import ru.mcsnapix.snapiclans.caching.actions.ResponseInviteAction
import ru.mcsnapix.snapiclans.caching.cache.ClanCaches
import ru.mcsnapix.snapiclans.extensions.send
import ru.mcsnapix.snapiclans.managers.RoleManager
import ru.mcsnapix.snapiclans.settings.Settings
import java.util.*

object InviteManager {
    private var pollTask: BukkitTask? = null

    fun enable() {
        pollTask = Bukkit.getScheduler().runTaskTimerAsynchronously(SnapiClans.instance, { pollMessages() }, 0L, 20L)
    }

    fun disable() {
        pollTask?.cancel()
        pollTask = null
    }

    private fun pollMessages() {
        DB.getResults("SELECT * FROM `clan_invite`")?.let { rows ->
            rows.forEach {
                val id = it.getInt("id")
                val clanId = it.getInt("clan_id")
                val sender = it.getString("sender")
                val receiver = it.getString("receiver")
                val time = it.getInt("time")

                if (time * 1000 >= Settings.config.inviteReplySeconds() * 1000 + System.currentTimeMillis()) {
                    ignore(sender, receiver)
                }
            }
        }
    }

    fun add(clan: Clan, sender: String, receiver: String) {
        DB.executeUpdate(
            "INSERT INTO `clan_invite`(`clan_id`, `sender`, `receiver`) VALUES (?, ?, ?)",
            clan.id,
            sender,
            receiver
        )
    }

    private fun get(sender: String, receiver: String): DbRow {
        return DB.getFirstRow("SELECT * FROM `clan_invite` WHERE `sender` = ? AND `receiver` = ?", sender, receiver)
    }

    fun accept(sender: String, receiver: Player) {
        val clan = ClanAPI.getUserClan(sender)
        val clanInDatabase = get(sender, receiver.name)

        remove(sender, receiver.name)

        if (clan == null || ClanCaches[clanInDatabase.getInt("clan_id")] != clan) {
            receiver.send(Settings.message.commands().accept().error())
            return
        }

        ClanAPI.createUser(receiver.name, clan, RoleManager.default)
        receiver.send(Settings.message.commands().accept().success())
        Messenger.sendOutgoingMessage(
            ResponseInviteAction(
                UUID.randomUUID(),
                sender,
                receiver.name,
                clan.name,
                InviteStatus.ACCEPT.name
            )
        )
    }

    fun decline(sender: String, receiver: Player) {
        val clan = ClanAPI.getUserClan(sender)
        val clanInDatabase = get(sender, receiver.name)

        remove(sender, receiver.name)

        if (clan == null || ClanCaches[clanInDatabase.getInt("clan_id")] != clan) {
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
                InviteStatus.DECLINE.name
            )
        )
    }

    fun ignore(sender: String, receiver: String) {
        val clan = ClanAPI.getUserClan(sender) ?: return

        remove(sender, receiver)
        Messenger.sendOutgoingMessage(
            ResponseInviteAction(
                UUID.randomUUID(),
                sender,
                receiver,
                clan.name,
                InviteStatus.IGNORE.name
            )
        )
    }

    fun remove(sender: String, receiver: String) {
        DB.executeUpdate("DELETE FROM `clan_invite` WHERE `sender` = ? AND `receiver` = ?", sender, receiver)
    }

    fun remove(sender: String) {
        DB.executeUpdate("DELETE FROM `clan_invite` WHERE `sender` = ?", sender)
    }

    fun remove(id: Int) {
        DB.executeUpdate("DELETE FROM `clan_invite` WHERE `id` = ?", id)
    }
}