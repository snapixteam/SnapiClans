package ru.mcsnapix.snapiclans.messenger

import co.aikar.idb.DB
import com.google.gson.*
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import ru.mcsnapix.snapiclans.SnapiClans
import java.util.*
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.math.max

object Messenger {
    private val scheduler = Bukkit.getScheduler()
    private val lock: ReadWriteLock = ReentrantReadWriteLock()
    private val gson: Gson = GsonBuilder().disableHtmlEscaping().create()
    private var lastId: Int = -1
    private var closed = false
    private var pollTask: BukkitTask? = null
    private var housekeepingTask: BukkitTask? = null

    fun enable() {
        DB.getFirstColumn<Int>("SELECT MAX(`id`) as `latest` FROM `clan_messenger`")?.let {
            lastId = it
        }
        pollTask = scheduler.runTaskTimerAsynchronously(SnapiClans.instance, { pollMessages() }, 0L, 20L)
        housekeepingTask =
            scheduler.runTaskTimerAsynchronously(SnapiClans.instance, { runHousekeeping() }, 0L, 20L * 30L)
    }

    fun disable() {
        close()
    }

    fun sendOutgoingMessage(action: Action) {
        lock.readLock().lock()
        if (closed) {
            lock.readLock().unlock()
            return
        }

        DB.executeUpdate(
            "INSERT INTO `clan_messenger` (`time`, `msg`) VALUES(NOW(), ?)",
            action.encode()
        )
        lock.readLock().unlock()
    }

    private fun pollMessages() {
        lock.readLock().lock()
        if (closed) {
            lock.readLock().unlock()
            return
        }

        DB.getFirstRow(
            "SELECT `id`, `msg` FROM `clan_messenger` WHERE `id` > ? AND (NOW() - `time` < 30)",
            lastId
        )?.apply {
            lastId = max(lastId, getInt("id"))
            val message: String = getString("msg")

            val parsed: JsonObject = gson.fromJson(message, JsonObject::class.java)
            val json = parsed.asJsonObject

            val idElement = json["id"]
            val id = UUID.fromString(idElement.asString)

            val typeElement = json["type"]
                ?: throw IllegalStateException("Incoming message has no type argument")
            val type = ActionType.valueOf(typeElement.asString)

            val content = json["content"]


            val action = type.decode(content, id)
            action.executeIncomingMessage()

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

    private fun close() {
        pollTask?.cancel()
        housekeepingTask?.cancel()
        pollTask = null
        housekeepingTask = null

        closed = true
    }


    fun encodeMessage(type: ActionType, id: UUID, content: JsonElement): String {
        val jsonObject = JsonObject()
        jsonObject.add("id", JsonPrimitive(id.toString()))
        jsonObject.add("type", JsonPrimitive(type.name))
        jsonObject.add("content", content)

        return gson.toJson(jsonObject)
    }
}