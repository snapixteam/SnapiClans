package ru.mcsnapix.snapiclans.messenger.actions

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import ru.mcsnapix.snapiclans.api.events.RemoveClanEvent
import ru.mcsnapix.snapiclans.callEvent
import ru.mcsnapix.snapiclans.database.ClanCache
import ru.mcsnapix.snapiclans.messenger.Action
import ru.mcsnapix.snapiclans.messenger.ActionType
import ru.mcsnapix.snapiclans.messenger.Messenger
import java.util.*

/**
 * @author Flaimer
 * @since 0.0.3
 */
class RemoveClanAction(id: UUID, val name: String) : Action(id, ActionType.REMOVE_CLAN) {
    override fun executeIncomingMessage() {
        runBlocking {
            val result = async {
                ClanCache.get { it.name == name }?.let {
                    ClanCache.remove(it)
                    callEvent(RemoveClanEvent(it))
                }
            }
            result.await()
        }
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