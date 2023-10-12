package ru.mcsnapix.snapiclans.messenger.message

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import ru.mcsnapix.snapiclans.extensions.encodeMessageAsString
import java.util.*

class InviteMessage(id: UUID, val inviter: String, val invited: String, val time: Long, val clan: String) : AbstractMessage(id) {
    override fun asEncodedString(): String {
        val jsonObject = JsonObject()
        jsonObject.add("inviter", JsonPrimitive(inviter))
        jsonObject.add("invited", JsonPrimitive(invited))
        jsonObject.add("time", JsonPrimitive(time))
        jsonObject.add("clan", JsonPrimitive(clan))
        return encodeMessageAsString(TYPE, id, jsonObject)
    }

    companion object {
        const val TYPE = "invitesend"

        fun decode(content: JsonElement?, id: UUID): InviteMessage {
            checkNotNull(content) { "Missing content" }

            val inviterElement = content.asJsonObject["inviter"]
                ?: throw IllegalStateException("Incoming message has no inviter argument: $content")
            val inviter = inviterElement.asString

            val invitedElement = content.asJsonObject["invited"]
                ?: throw IllegalStateException("Incoming message has no invited argument: $content")
            val invited = invitedElement.asString

            val timeElement = content.asJsonObject["time"]
                ?: throw IllegalStateException("Incoming message has no time argument: $content")
            val time = timeElement.asLong

            val clanElement = content.asJsonObject["clan"]
                ?: throw IllegalStateException("Incoming message has no clan argument: $content")
            val clan = clanElement.asString

            return InviteMessage(id, inviter, invited, time, clan)
        }
    }
}