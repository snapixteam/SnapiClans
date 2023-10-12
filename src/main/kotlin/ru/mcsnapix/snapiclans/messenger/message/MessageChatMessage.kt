package ru.mcsnapix.snapiclans.messenger.message

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import ru.mcsnapix.snapiclans.extensions.encodeMessageAsString
import java.util.*

class MessageChatMessage(id: UUID, val sender: String, val clan: String, val message: String) : AbstractMessage(id) {
    override fun asEncodedString(): String {
        val jsonObject = JsonObject()
        jsonObject.add("sender", JsonPrimitive(sender))
        jsonObject.add("clan", JsonPrimitive(clan))
        jsonObject.add("message", JsonPrimitive(message))
        return encodeMessageAsString(TYPE, id, jsonObject)
    }

    companion object {
        const val TYPE = "messagechat"

        fun decode(content: JsonElement?, id: UUID): MessageChatMessage {
            checkNotNull(content) { "Missing content" }

            val senderElement = content.asJsonObject["sender"]
                ?: throw IllegalStateException("Incoming message has no sender argument: $content")
            val sender = senderElement.asString

            val clanElement = content.asJsonObject["clan"]
                ?: throw IllegalStateException("Incoming message has no clan argument: $content")
            val clan = clanElement.asString

            val messageElement = content.asJsonObject["message"]
                ?: throw IllegalStateException("Incoming message has no message argument: $content")
            val message = messageElement.asString

            return MessageChatMessage(id, sender, clan, message)
        }
    }
}