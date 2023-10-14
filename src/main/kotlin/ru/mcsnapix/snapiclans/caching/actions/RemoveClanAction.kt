package ru.mcsnapix.snapiclans.caching.actions

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import ru.mcsnapix.snapiclans.api.ClanAPI
import ru.mcsnapix.snapiclans.api.events.RemoveClanEvent
import ru.mcsnapix.snapiclans.caching.Action
import ru.mcsnapix.snapiclans.caching.ActionType
import ru.mcsnapix.snapiclans.caching.Messenger
import ru.mcsnapix.snapiclans.caching.cache.ClanCaches
import java.util.*

class RemoveClanAction(id: UUID, val name: String) : Action(id) {
    override val type: ActionType = ActionType.REMOVE_CLAN

    override fun executeIncomingMessage() {
        ClanCaches[name]?.let {
            ClanAPI.callEvent(RemoveClanEvent(it))
        }
        ClanCaches.remove(name)
    }

    override fun encode(): String {
        val jsonObject = JsonObject()

        jsonObject.add("name", JsonPrimitive(name))

        return Messenger.encodeMessage(type, id, jsonObject)
    }

    companion object {
        fun decode(content: JsonElement?, id: UUID): RemoveClanAction {
            checkNotNull(content) { "Missing content" }

            val name = elementAsString("name", content)

            return RemoveClanAction(id, name)
        }
    }
}