package ru.mcsnapix.snapiclans.caching.actions

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import ru.mcsnapix.snapiclans.api.ClanAPI
import ru.mcsnapix.snapiclans.api.events.SendMessageEvent
import ru.mcsnapix.snapiclans.caching.Action
import ru.mcsnapix.snapiclans.caching.ActionType
import ru.mcsnapix.snapiclans.caching.Messenger
import ru.mcsnapix.snapiclans.caching.cache.ClanCaches
import ru.mcsnapix.snapiclans.caching.cache.UserCaches
import java.util.*

/**
 * @author Flaimer
 * @since 0.0.3
 */
class SendMessageAction(id: UUID, val sender: String, val clan: String, val message: String) : Action(id) {
    override val type: ActionType = ActionType.SEND_MESSAGE

    override fun executeIncomingMessage() {
        ClanCaches[clan]?.let {
            val message = message

            UserCaches[sender]?.let { user ->
                ClanAPI.callEvent(SendMessageEvent(it, user, message))
            }

            it.members.forEach { user ->
                if (user.name != sender) {
                    user.player?.sendMessage(message)
                }
            }
        }
    }

    override fun encode(): String {
        val jsonObject = JsonObject()

        jsonObject.add("sender", JsonPrimitive(sender))
        jsonObject.add("clan", JsonPrimitive(clan))
        jsonObject.add("message", JsonPrimitive(message))

        return Messenger.encodeMessage(type, id, jsonObject)
    }

    companion object {
        fun decode(content: JsonElement?, id: UUID): SendMessageAction {
            checkNotNull(content) { "Missing content" }

            val sender = elementAsString("sender", content)
            val clan = elementAsString("clan", content)
            val message = elementAsString("message", content)

            return SendMessageAction(id, sender, clan, message)
        }
    }
}