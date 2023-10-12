package ru.mcsnapix.snapiclansold.registry.invite

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import ru.mcsnapix.snapiclansold.SnapiClans
import ru.mcsnapix.snapiclansold.api.clans.Clan
import ru.mcsnapix.snapiclansold.api.events.user.invite.IgnoreUserEvent
import ru.mcsnapix.snapiclansold.api.events.user.invite.InviteUserEvent
import ru.mcsnapix.snapiclansold.messenger.SQLMessenger
import ru.mcsnapix.snapiclansold.messenger.message.UserUpdateMessage
import ru.mcsnapix.snapiclans.settings.Settings
import java.util.*

object InvitationRegistry {
    private var task: BukkitTask? = null
    private val table: Table<String, String, Invite> = HashBasedTable.create()

    fun enable() {
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(SnapiClans.instance, {
            table.cellSet().forEach { table ->
                table.value?.let { invite ->
                    if (System.currentTimeMillis() - invite.timestamp >= Settings.config.inviteReplySeconds() * 1000L) {
                        SQLMessenger.sendOutgoingMessage(UserUpdateMessage(UUID.randomUUID(), user.username))
                        IgnoreUserEvent(Bukkit.getPlayer(table.columnKey), invite.clan)
                    }
                }
            }
        }, 0L, 80L)
    }

    fun createInvite(inviter: String, invited: String, clan: Clan) {
        InviteUserEvent(inviter, invited, clan)
    }

    fun add(inviter: String, invited: String, clan: Clan, timeMillis: Long) {
        table.put(inviter, invited, Invite(clan, timeMillis))
    }

    fun remove(inviter: String, invited: String) {
        table.remove(inviter, invited)
    }

    fun disable() {
        table.clear()
        task?.cancel()
    }
}
//object InvitationRegistry {
//    private var task: BukkitTask? = null
//    private val table: Table<String, String, Invite> = HashBasedTable.create()
//
//    fun enable() {
//        task = Bukkit.getScheduler().runTaskTimerAsynchronously(SnapiClans.instance, {
//            table.cellSet().forEach { table ->
//                table.value?.let {
//                    if (System.currentTimeMillis() - it.timestamp >= Settings.config.inviteReplySeconds() * 1000L) {
//                        remove(table.rowKey, table.columnKey)
//                        table.columnKey?.let { invited ->
//                            DeclineUserEvent(Bukkit.getPlayer(invited), it.clan)
//                        }
//                    }
//                }
//            }
//        }, 0L, 80L)
//    }
//
//    fun addInvite(inviter: String, invited: String, clan: Clan, timeMillis: Long) {
//        table.put(inviter, invited, Invite(clan, timeMillis))
//    }
//
//    fun add(inviter: String, invited: String, clan: Clan) {
//        SQLMessenger.sendOutgoingMessage(InviteMessage(UUID.randomUUID(), inviter, invited, System.currentTimeMillis(), clan.name))
//    }
//
//    operator fun get(inviter: String?, invited: String?): Invite = table[inviter, invited]
//
//    fun remove(inviter: String?, invited: String?) {
//        table.remove(inviter, invited)
//    }
//
//    fun disable() {
//        table.clear()
//        task?.cancel()
//    }
//}