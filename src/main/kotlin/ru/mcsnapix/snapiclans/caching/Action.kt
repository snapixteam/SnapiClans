package ru.mcsnapix.snapiclans.caching

import com.google.gson.JsonElement
import java.util.*

abstract class Action(val id: UUID) {
    abstract val type: ActionType

    abstract fun executeIncomingMessage()
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