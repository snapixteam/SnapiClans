package ru.mcsnapix.snapiclans.messenger.actions

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import ru.mcsnapix.snapiclans.api.events.CreateUserEvent
import ru.mcsnapix.snapiclans.callEvent
import ru.mcsnapix.snapiclans.database.UserCache
import ru.mcsnapix.snapiclans.messenger.Action
import ru.mcsnapix.snapiclans.messenger.ActionType
import ru.mcsnapix.snapiclans.messenger.Messenger.encodeMessage
import java.util.*

/**
 * @author Flaimer
 * @since 0.0.3
 */
class CreateUserAction(id: UUID, val name: String) : Action(id, ActionType.CREATE_USER) {
    override fun executeIncomingMessage() {
        runBlocking {
            val result = async {
                UserCache.get { it.name == name }?.let {
                    UserCache.add(it)
                    callEvent(CreateUserEvent(it))
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
        fun decode(content: JsonElement?, id: UUID): CreateUserAction {
            checkNotNull(content) { "Missing content" }

            val name = elementAsString("name", content)

            return CreateUserAction(id, name)
        }
    }
}