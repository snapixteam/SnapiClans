package ru.mcsnapix.snapiclans.caching.actions

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import ru.mcsnapix.snapiclans.api.ClanAPI
import ru.mcsnapix.snapiclans.api.events.CreateUserEvent
import ru.mcsnapix.snapiclans.caching.Action
import ru.mcsnapix.snapiclans.caching.ActionType
import ru.mcsnapix.snapiclans.caching.Messenger.encodeMessage
import ru.mcsnapix.snapiclans.caching.cache.UserCaches
import java.util.*

class CreateUserAction(id: UUID, val name: String) : Action(id) {
    override val type: ActionType = ActionType.CREATE_USER

    override fun executeIncomingMessage() {
        UserCaches.add(name)
        UserCaches[name]?.let {
            ClanAPI.callEvent(CreateUserEvent(it))
        }
    }

    override fun encode(): String {
        val jsonObject = JsonObject()

        jsonObject.add("name", JsonPrimitive(name))

        return encodeMessage(type, id, jsonObject)
    }

    companion object {
        fun decode(content: JsonElement?, id: UUID): CreateUserAction {
            checkNotNull(content) { "Missing content" }

            val name = elementAsString("name", content)

            return CreateUserAction(id, name)
        }
    }
}