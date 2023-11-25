package ru.mcsnapix.snapiclans.messenger.actions

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import ru.mcsnapix.snapiclans.api.events.ResponseInviteEvent
import ru.mcsnapix.snapiclans.callEvent
import ru.mcsnapix.snapiclans.database.ClanCache
import ru.mcsnapix.snapiclans.managers.InviteStatus
import ru.mcsnapix.snapiclans.messenger.Action
import ru.mcsnapix.snapiclans.messenger.ActionType
import ru.mcsnapix.snapiclans.messenger.Messenger.encodeMessage
import java.util.*

/**
 * @author Flaimer
 * @since 0.0.4
 */
class ResponseInviteAction(
    id: UUID,
    val sender: String,
    val receiver: String,
    val clan: String,
    val status: InviteStatus
) :
    Action(id, ActionType.RESPONSE_INVITE) {
    override fun executeIncomingMessage() {
        runBlocking {
            val result = async {
                ClanCache.get { it.name == clan }?.let {
                    callEvent(ResponseInviteEvent(it, sender, receiver, status))
                }
            }
            result.await()
        }
    }

    override fun encode(): String {
        val jsonObject = JsonObject()

        jsonObject.add("sender", JsonPrimitive(sender))
        jsonObject.add("receiver", JsonPrimitive(receiver))
        jsonObject.add("clan", JsonPrimitive(clan))
        jsonObject.add("status", JsonPrimitive(status.name))

        return encodeMessage(type, id, jsonObject)
    }

    companion object {
        fun decode(content: JsonElement?, id: UUID): ResponseInviteAction {
            checkNotNull(content) { "Missing content" }

            val sender = elementAsString("sender", content)
            val receiver = elementAsString("receiver", content)
            val clan = elementAsString("clan", content)
            val status = elementAsString("status", content)

            return ResponseInviteAction(id, sender, receiver, clan, InviteStatus.valueOf(status))
        }
    }
}