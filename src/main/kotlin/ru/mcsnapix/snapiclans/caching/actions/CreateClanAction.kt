package ru.mcsnapix.snapiclans.caching.actions

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import ru.mcsnapix.snapiclans.api.ClanAPI
import ru.mcsnapix.snapiclans.api.events.CreateClanEvent
import ru.mcsnapix.snapiclans.caching.Action
import ru.mcsnapix.snapiclans.caching.ActionType
import ru.mcsnapix.snapiclans.caching.Messenger.encodeMessage
import ru.mcsnapix.snapiclans.caching.cache.ClanCaches
import java.util.*

/**
 * @author Flaimer
 * @since 0.0.3
 */
class CreateClanAction(id: UUID, val name: String) : Action(id) {
    override val type: ActionType = ActionType.CREATE_CLAN

    override fun executeIncomingMessage() {
        ClanCaches.add(name)
        ClanCaches[name]?.let {
            ClanAPI.callEvent(CreateClanEvent(it))
        }
    }

    override fun encode(): String {
        val jsonObject = JsonObject()

        jsonObject.add("name", JsonPrimitive(name))

        return encodeMessage(type, id, jsonObject)
    }

    companion object {
        fun decode(content: JsonElement?, id: UUID): CreateClanAction {
            checkNotNull(content) { "Missing content" }

            val name = elementAsString("name", content)

            return CreateClanAction(id, name)
        }
    }
}