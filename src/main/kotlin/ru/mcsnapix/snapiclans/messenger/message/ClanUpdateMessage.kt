package ru.mcsnapix.snapiclans.messenger.message

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import ru.mcsnapix.snapiclans.extensions.encodeMessageAsString
import java.util.*

class ClanUpdateMessage(id: UUID, val name: String) : AbstractMessage(id) {
    override fun asEncodedString(): String {
        val jsonObject = JsonObject()
        jsonObject.add("name", JsonPrimitive(name))
        return encodeMessageAsString(TYPE, id, jsonObject)
    }

    companion object {
        const val TYPE = "clanupdate"

        fun decode(content: JsonElement?, id: UUID): ClanUpdateMessage {
            checkNotNull(content) { "Missing content" }

            val nameElement = content.asJsonObject["name"]
                ?: throw IllegalStateException("Incoming message has no name argument: $content")

            val name = nameElement.asString
            return ClanUpdateMessage(id, name)
        }
    }
}