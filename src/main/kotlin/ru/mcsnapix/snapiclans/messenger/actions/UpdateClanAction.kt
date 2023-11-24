package ru.mcsnapix.snapiclans.caching.actions

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import ru.mcsnapix.snapiclans.database.ClanCache
import ru.mcsnapix.snapiclans.database.ClanService
import ru.mcsnapix.snapiclans.messenger.Action
import ru.mcsnapix.snapiclans.messenger.ActionType
import ru.mcsnapix.snapiclans.messenger.Messenger
import java.util.*

/**
 * @author Flaimer
 * @since 0.0.3
 */
class UpdateClanAction(id: UUID, val name: String) : Action(id, ActionType.UPDATE_CLAN) {
    override fun executeIncomingMessage() {
        runBlocking {
            val result = async {
                ClanService.read(name)?.let {
                    ClanCache.reload(it)
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
        fun decode(content: JsonElement?, id: UUID): UpdateClanAction {
            checkNotNull(content) { "Missing content" }

            val name = elementAsString("name", content)

            return UpdateClanAction(id, name)
        }
    }
}