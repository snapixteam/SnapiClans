package ru.mcsnapix.snapiclans.caching.actions

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import ru.mcsnapix.snapiclans.api.ClanAPI
import ru.mcsnapix.snapiclans.api.events.ResponseInviteEvent
import ru.mcsnapix.snapiclans.caching.Action
import ru.mcsnapix.snapiclans.caching.ActionType
import ru.mcsnapix.snapiclans.caching.Messenger.encodeMessage
import ru.mcsnapix.snapiclans.caching.cache.ClanCaches
import ru.mcsnapix.snapiclans.managers.invite.InviteStatus
import java.util.*

/**
 * @author Flaimer
 * @since 0.0.4
 */
class ResponseInviteAction(id: UUID, val sender: String, val receiver: String, val clan: String, val status: String) :
    Action(id) {
    override val type: ActionType = ActionType.RESPONSE_INVITE

    override fun executeIncomingMessage() {
        val inviteStatus = InviteStatus.valueOf(status)
        ClanCaches[clan]?.let {
            ClanAPI.callEvent(ResponseInviteEvent(it, sender, receiver, inviteStatus))
        }
    }

    override fun encode(): String {
        val jsonObject = JsonObject()

        jsonObject.add("sender", JsonPrimitive(sender))
        jsonObject.add("receiver", JsonPrimitive(receiver))
        jsonObject.add("clan", JsonPrimitive(clan))
        jsonObject.add("status", JsonPrimitive(status))

        return encodeMessage(type, id, jsonObject)
    }

    companion object {
        fun decode(content: JsonElement?, id: UUID): ResponseInviteAction {
            checkNotNull(content) { "Missing content" }

            val sender = elementAsString("sender", content)
            val receiver = elementAsString("receiver", content)
            val clan = elementAsString("clan", content)
            val status = elementAsString("status", content)

            return ResponseInviteAction(id, sender, receiver, clan, status)
        }
    }
}