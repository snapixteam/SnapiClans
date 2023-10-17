package ru.mcsnapix.snapiclans.caching

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.util.*

/**
 * @author Flaimer
 * @since 0.0.3
 */
abstract class Action(val id: UUID) {
    abstract val type: ActionType

    /**
     * Perform an incoming action from the database
     *
     * @see Messenger
     */
    abstract fun executeIncomingMessage()

    /**
     * Creates a json object containing various parameters so that other servers can process the information
     *
     * @see JsonObject
     * @see Messenger.encodeMessage
     */
    abstract fun encode(): String

    companion object {
        fun elementAsString(name: String, content: JsonElement): String {
            val element = content.asJsonObject[name]
                ?: throw IllegalStateException("Incoming message has no name argument: $content")
            return element.asString
        }

        fun elementAsLong(name: String, content: JsonElement): Long {
            val element = content.asJsonObject[name]
                ?: throw IllegalStateException("Incoming message has no name argument: $content")
            return element.asLong
        }

        fun elementAsInt(name: String, content: JsonElement): Int {
            val element = content.asJsonObject[name]
                ?: throw IllegalStateException("Incoming message has no name argument: $content")
            return element.asInt
        }
    }
}