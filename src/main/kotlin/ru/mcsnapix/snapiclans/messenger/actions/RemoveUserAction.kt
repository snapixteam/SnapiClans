package ru.mcsnapix.snapiclans.messenger.actions

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import ru.mcsnapix.snapiclans.api.events.RemoveUserEvent
import ru.mcsnapix.snapiclans.callEvent
import ru.mcsnapix.snapiclans.database.UserCache
import ru.mcsnapix.snapiclans.database.UserService
import ru.mcsnapix.snapiclans.messenger.Action
import ru.mcsnapix.snapiclans.messenger.ActionType
import ru.mcsnapix.snapiclans.messenger.Messenger.encodeMessage
import java.util.*

/**
 * @author Flaimer
 * @since 0.0.3
 */
class RemoveUserAction(id: UUID, val name: String) : Action(id, ActionType.REMOVE_USER) {
    override fun executeIncomingMessage() {
        runBlocking {
            val result = async {
                UserService.read(name)?.let {
                    UserService.delete(it.name)
                    UserCache.remove(it)
                    callEvent(RemoveUserEvent(it))
                }
            }
            result.await()
        }
    }

    override fun encode(): String {
        val jsonObject = JsonObject()

        jsonObject.add("name", JsonPrimitive(name))

        return encodeMessage(type, id, jsonObject)
    }

    companion object {
        fun decode(content: JsonElement?, id: UUID): RemoveUserAction {
            checkNotNull(content) { "Missing content" }

            val name = elementAsString("name", content)

            return RemoveUserAction(id, name)
        }
    }
}