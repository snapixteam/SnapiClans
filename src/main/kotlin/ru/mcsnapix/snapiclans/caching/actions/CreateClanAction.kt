package ru.mcsnapix.snapiclans.caching.actions

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import ru.mcsnapix.snapiclans.api.ClanAPI
import ru.mcsnapix.snapiclans.api.events.CreateClanEvent
import ru.mcsnapix.snapiclans.caching.Action
import ru.mcsnapix.snapiclans.caching.ActionType
import ru.mcsnapix.snapiclans.caching.Messenger.encodeMessage
import java.util.*

class CreateClanAction(id: UUID, val name: String) : Action(id) {
    override val type: ActionType = ActionType.CREATE_CLAN

    override fun executeIncomingMessage() {
        ClanAPI.callEvent(CreateClanEvent(id, name))
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