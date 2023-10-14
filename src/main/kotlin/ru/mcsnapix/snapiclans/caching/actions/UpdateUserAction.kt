package ru.mcsnapix.snapiclans.caching.actions

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import ru.mcsnapix.snapiclans.caching.Action
import ru.mcsnapix.snapiclans.caching.ActionType
import ru.mcsnapix.snapiclans.caching.Messenger
import ru.mcsnapix.snapiclans.caching.cache.UserCaches
import java.util.*

class UpdateUserAction(id: UUID, val name: String) : Action(id) {
    override val type: ActionType = ActionType.UPDATE_USER

    override fun executeIncomingMessage() {
        UserCaches.refresh(name)
    }

    override fun encode(): String {
        val jsonObject = JsonObject()

        jsonObject.add("name", JsonPrimitive(name))

        return Messenger.encodeMessage(type, id, jsonObject)
    }

    companion object {
        fun decode(content: JsonElement?, id: UUID): UpdateUserAction {
            checkNotNull(content) { "Missing content" }

            val name = elementAsString("name", content)

            return UpdateUserAction(id, name)
        }
    }
}