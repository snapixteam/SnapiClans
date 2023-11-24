package ru.mcsnapix.snapiclans.messenger

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.util.*

/**
 * @author Flaimer
 * @since 0.0.3
 */
abstract class Action(val id: UUID, val type: ActionType) {
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
        /**
         * A helper method that returns the value of a json element as a string.
         * If the element is null or not a string, it throws an exception.
         *
         * @param name    the name of the json element
         * @param content the json element to get the value from
         * @return the string value of the json element
         * @throws IllegalStateException if the element is null or not a string
         */
        @JvmStatic
        fun elementAsString(name: String, content: JsonElement): String {
            val element = content.asJsonObject[name]
                ?: throw IllegalStateException("Incoming message has no name argument: $content")
            return element.asString
        }

        /**
         * A helper method that returns the value of a json element as a long.
         * If the element is null or not a long, it throws an exception.
         *
         * @param name    the name of the json element
         * @param content the json element to get the value from
         * @return the long value of the json element
         * @throws IllegalStateException if the element is null or not a long
         */
        @JvmStatic
        fun elementAsLong(name: String, content: JsonElement): Long {
            val element = content.asJsonObject[name]
                ?: throw IllegalStateException("Incoming message has no name argument: $content")
            return element.asLong
        }

        /**
         * A helper method that returns the value of a json element as a json array.
         * If the element is null or not a json array, it throws an exception.
         *
         * @param name    the name of the json element
         * @param content the json element to get the value from
         * @return the json array value of the json element
         * @throws IllegalStateException if the element is null or not a json array
         */
        @JvmStatic
        fun elementAsJsonArray(name: String, content: JsonElement): JsonArray {
            val element = content.asJsonObject[name]
                ?: throw IllegalStateException("Incoming message has no name argument: $content")
            return element.asJsonArray
        }

        /**
         * A helper method that returns the value of a json element as an int.
         * If the element is null or not an int, it throws an exception.
         *
         * @param name    the name of the json element
         * @param content the json element to get the value from
         * @return the int value of the json element
         * @throws IllegalStateException if the element is null or not an int
         */
        @JvmStatic
        fun elementAsInt(name: String, content: JsonElement): Int {
            val element = content.asJsonObject[name]
                ?: throw IllegalStateException("Incoming message has no name argument: $content")
            return element.asInt
        }
    }
}