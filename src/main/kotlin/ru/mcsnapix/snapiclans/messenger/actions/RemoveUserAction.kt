package ru.mcsnapix.snapiclans.caching.actions

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import ru.mcsnapix.snapiclans.api.ClanAPI
import ru.mcsnapix.snapiclans.api.events.RemoveUserEvent
import ru.mcsnapix.snapiclans.caching.Action
import ru.mcsnapix.snapiclans.caching.ActionType
import ru.mcsnapix.snapiclans.caching.Messenger
import ru.mcsnapix.snapiclans.caching.cache.UserCaches
import java.util.*

/**
 * @author Flaimer
 * @since 0.0.3
 */
class RemoveUserAction(id: UUID, val name: String) : Action(id) {
    override val type: ActionType = ActionType.REMOVE_USER

    override fun executeIncomingMessage() {
        UserCaches[name]?.let {
            ClanAPI.callEvent(RemoveUserEvent(it))
        }
        UserCaches.remove(name)
    }

    override fun encode(): String {
        val jsonObject = JsonObject()

        jsonObject.add("name", JsonPrimitive(name))

        return Messenger.encodeMessage(type, id, jsonObject)
    }

    companion object {
        fun decode(content: JsonElement?, id: UUID): RemoveUserAction {
            checkNotNull(content) { "Missing content" }

            val name = elementAsString("name", content)

            return RemoveUserAction(id, name)
        }
    }
}