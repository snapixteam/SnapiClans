package ru.mcsnapix.snapiclans.messenger

import co.aikar.idb.DB
import com.google.gson.JsonObject
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import ru.mcsnapix.snapiclans.Part
import ru.mcsnapix.snapiclans.SnapiClans
import ru.mcsnapix.snapiclans.extensions.Gson
import ru.mcsnapix.snapiclans.messenger.message.AbstractMessage
import ru.mcsnapix.snapiclans.messenger.message.ClanUpdateMessage
import ru.mcsnapix.snapiclans.messenger.message.OutgoingMessage
import ru.mcsnapix.snapiclans.registry.ClansRegistry
import java.util.*
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.math.max

object SQLMessenger : Part() {
    private val scheduler = Bukkit.getScheduler()
    private val lock: ReadWriteLock = ReentrantReadWriteLock()
    private var lastId: Int = -1
    private var closed = false
    private var pollTask: BukkitTask? = null
    private var housekeepingTask: BukkitTask? = null

    override fun enable() {
        DB.getFirstColumn<Int>("SELECT MAX(`id`) as `latest` FROM `clan_messenger`")?.let {
            lastId = it
        }
        pollTask = scheduler.runTaskTimerAsynchronously(SnapiClans.instance, { pollMessages() }, 0L, 20L)
        housekeepingTask =
            scheduler.runTaskTimerAsynchronously(SnapiClans.instance, { runHousekeeping() }, 0L, 20L * 30L)
    }

    override fun disable() {
        close()
    }

    private fun pollMessages() {
        lock.readLock().lock()
        if (closed) {
            lock.readLock().unlock()
            return
        }

        DB.getFirstRow("SELECT `id`, `msg` FROM `clan_messenger` WHERE `id` > ? AND (NOW() - `time` < 30)", lastId)
            ?.let {
                lastId = max(lastId, it.getInt("id"))
                val message: String = it.getString("msg")

                consumeIncomingMessage(message)

                lock.readLock().unlock()
            }
    }

    private fun runHousekeeping() {
        lock.readLock().lock()
        if (closed) {
            lock.readLock().unlock()
            return
        }

        DB.executeUpdate("DELETE FROM `clan_messenger` WHERE (NOW() - `time` > 60)")
        lock.readLock().unlock()
    }

    fun sendOutgoingMessage(outgoingMessage: OutgoingMessage) {
        lock.readLock().lock()
        if (closed) {
            lock.readLock().unlock()
            return
        }

        DB.executeUpdate(
            "INSERT INTO `clan_messenger` (`time`, `msg`) VALUES(NOW(), ?)",
            outgoingMessage.asEncodedString()
        )
        lock.readLock().unlock()
    }

    private fun close() {
        pollTask?.cancel()
        housekeepingTask?.cancel()
        pollTask = null
        housekeepingTask = null

        closed = true
    }

    private fun consumeIncomingMessage(encodedString: String) {
        val parsed: JsonObject = Gson.fromJson(encodedString, JsonObject::class.java)
        val json = parsed.asJsonObject

        val idElement = json["id"]
        val id = UUID.fromString(idElement.asString)


        val typeElement = json["type"]
            ?: throw IllegalStateException("Incoming message has no type argument: $encodedString")
        val type = typeElement.asString


        val content = json["content"]


        val decoded: AbstractMessage? = when (type) {
            ClanUpdateMessage.TYPE -> ClanUpdateMessage.decode(content, id)
            else -> null
        }

        decoded?.let { processIncomingMessage(it) }
    }

    private fun processIncomingMessage(message: AbstractMessage) {
        if (message is ClanUpdateMessage) {
            ClansRegistry.update(message.name)
        }
    }
}