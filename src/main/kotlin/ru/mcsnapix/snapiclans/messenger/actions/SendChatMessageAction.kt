package ru.mcsnapix.snapiclans.messenger.actions

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import ru.mcsnapix.snapiclans.api.events.SendChatMessageEvent
import ru.mcsnapix.snapiclans.callEvent
import ru.mcsnapix.snapiclans.database.ClanCache
import ru.mcsnapix.snapiclans.database.UserCache
import ru.mcsnapix.snapiclans.messenger.Action
import ru.mcsnapix.snapiclans.messenger.ActionType
import ru.mcsnapix.snapiclans.messenger.Messenger.encodeMessage
import java.util.*

/**
 * @author Flaimer
 * @since 0.0.3
 */
class SendChatMessageAction(id: UUID, val sender: String, val clan: String, val message: String) :
    Action(id, ActionType.SEND_MESSAGE) {
    override fun executeIncomingMessage() {
        runBlocking {
            val result = async {
                ClanCache.get { it.name == clan }?.let {
                    UserCache.get { it.name == sender }?.let { user ->
                        callEvent(SendChatMessageEvent(it, user, message))
                    }
                }
            }
            result.await()
        }
    }

    override fun encode(): String {
        val jsonObject = JsonObject()

        jsonObject.add("sender", JsonPrimitive(sender))
        jsonObject.add("clan", JsonPrimitive(clan))
        jsonObject.add("message", JsonPrimitive(message))

        return encodeMessage(type, id, jsonObject)
    }

    companion object {
        fun decode(content: JsonElement?, id: UUID): SendChatMessageAction {
            checkNotNull(content) { "Missing content" }

            val sender = elementAsString("sender", content)
            val clan = elementAsString("clan", content)
            val message = elementAsString("message", content)

            return SendChatMessageAction(id, sender, clan, message)
        }
    }
}