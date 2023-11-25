package ru.mcsnapix.snapiclans.messenger

import com.google.gson.*
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import ru.mcsnapix.snapiclans.SnapiClans
import ru.mcsnapix.snapiclans.database.executeQuery
import ru.mcsnapix.snapiclans.database.executeUpdate
import java.util.*
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.math.max

object Messenger {
    private val scheduler = Bukkit.getScheduler()
    private val gson: Gson = GsonBuilder().disableHtmlEscaping().create()
    private var lastId: Int = -1
    private var pollTask: BukkitTask? = null
    private var housekeepingTask: BukkitTask? = null

    fun enable() {
        executeQuery("SELECT MAX(`id`) as `latest` FROM `clan_messenger`") {
            lastId = it.getInt(1)
        }
        pollTask = scheduler.runTaskTimerAsynchronously(SnapiClans.instance, { pollMessages() }, 0L, 20L)
        housekeepingTask =
            scheduler.runTaskTimerAsynchronously(SnapiClans.instance, { runHousekeeping() }, 0L, 20L * 30L)
    }

    fun disable() {
        close()
    }

    fun sendOutgoingMessage(action: Action) {
        executeUpdate("INSERT INTO `clan_messenger` (`time`, `msg`) VALUES(NOW(), '${action.encode()}')")
    }

    private fun pollMessages() {
        executeQuery("SELECT `id`, `msg` FROM `clan_messenger` WHERE `id` > '$lastId' AND (NOW() - `time` < 30)") {
            lastId = max(lastId, it.getInt("id"))
            val message: String = it.getString("msg")

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
        }
    }

    private fun runHousekeeping() {
        executeUpdate("DELETE FROM `clan_messenger` WHERE (NOW() - `time` > 60)")
    }

    private fun close() {
        pollTask?.cancel()
        housekeepingTask?.cancel()
        pollTask = null
        housekeepingTask = null
    }


    fun encodeMessage(type: ActionType, id: UUID, content: JsonElement): String {
        val jsonObject = JsonObject()
        jsonObject.add("id", JsonPrimitive(id.toString()))
        jsonObject.add("type", JsonPrimitive(type.name))
        jsonObject.add("content", content)

        return gson.toJson(jsonObject)
    }
}