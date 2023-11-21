package ru.mcsnapix.snapiclans.managers.invite

import co.aikar.idb.DB
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import ru.mcsnapix.snapiclans.SnapiClans
import ru.mcsnapix.snapiclans.api.ClanAPI
import ru.mcsnapix.snapiclans.caching.Messenger
import ru.mcsnapix.snapiclans.caching.actions.ResponseInviteAction
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

    fun accept(sender: String, receiver: String) {
        val clan = ClanAPI.getUserClan(sender)

        if (clan == null) {
            val player = Bukkit.getPlayer(receiver) ?: return
            player.send(Settings.message.commands().accept().error())
            return
        }

        ClanAPI.createUser(receiver, clan, RoleManager.default)
        Messenger.sendOutgoingMessage(ResponseInviteAction(UUID.randomUUID(), sender, receiver, clan.name, InviteStatus.ACCEPT.name))
    }

    fun decline(sender: String, receiver: String) {
        // TODO: Удалять invite и отправить Messenger
    }

    fun ignore(sender: String, receiver: String) {
        val player = Bukkit.getPlayer(sender) ?: return
        // TODO: Удалять invite и отправить Messenger о Ignore
        remove(sender, receiver)
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