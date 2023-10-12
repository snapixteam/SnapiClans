package ru.mcsnapix.snapiclans.messenger.message

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import ru.mcsnapix.snapiclans.extensions.encodeMessageAsString
import ru.mcsnapix.snapiclans.registry.invite.InviteStatus
import java.util.*

class ResponseInvitationMessage(id: UUID, val inviter: String, val invited: String, val clan: String, val status: InviteStatus) : AbstractMessage(id) {
    override fun asEncodedString(): String {
        val jsonObject = JsonObject()
        jsonObject.add("inviter", JsonPrimitive(inviter))
        jsonObject.add("invited", JsonPrimitive(invited))
        jsonObject.add("clan", JsonPrimitive(clan))
        jsonObject.add("status", JsonPrimitive(status.name))
        return encodeMessageAsString(TYPE, id, jsonObject)
    }

    companion object {
        const val TYPE = "responseinvitation"

        fun decode(content: JsonElement?, id: UUID): ResponseInvitationMessage {
            checkNotNull(content) { "Missing content" }

            val inviterElement = content.asJsonObject["inviter"]
                ?: throw IllegalStateException("Incoming message has no inviter argument: $content")
            val inviter = inviterElement.asString

            val invitedElement = content.asJsonObject["invited"]
                ?: throw IllegalStateException("Incoming message has no invited argument: $content")
            val invited = invitedElement.asString

            val clanElement = content.asJsonObject["clan"]
                ?: throw IllegalStateException("Incoming message has no clan argument: $content")
            val clan = clanElement.asString

            val statusElement = content.asJsonObject["status"]
                ?: throw IllegalStateException("Incoming message has no status argument: $content")
            val status = InviteStatus.valueOf(statusElement.asString)

            return ResponseInvitationMessage(id, inviter, invited, clan, status)
        }
    }
}