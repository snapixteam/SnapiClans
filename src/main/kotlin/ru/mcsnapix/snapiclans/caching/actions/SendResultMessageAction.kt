package ru.mcsnapix.snapiclans.caching.actions

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.bukkit.Bukkit
import ru.mcsnapix.snapiclans.caching.Action
import ru.mcsnapix.snapiclans.caching.ActionType
import ru.mcsnapix.snapiclans.Placeholder
import ru.mcsnapix.snapiclans.PlaceholderManager
import ru.mcsnapix.snapiclans.PlaceholderSerializer
import ru.mcsnapix.snapiclans.caching.Messenger
import ru.mcsnapix.snapiclans.extensions.send
import java.util.*

class SendResultMessageAction(id: UUID, val receiver: String, val message: String, val placeholders: List<Placeholder<String>>) : Action(id) {
    override val type: ActionType = ActionType.SEND_RESULT_MESSAGE

    override fun executeIncomingMessage() {
        val player = Bukkit.getPlayer(receiver) ?: return
        val message = PlaceholderManager.parse(message, placeholders)
        player.send(message)
    }

    override fun encode(): String {
        val jsonObject = JsonObject()

        jsonObject.add("receiver", JsonPrimitive(receiver))
        jsonObject.add("message", JsonPrimitive(message))

        val jsonArray = JsonArray()
        for (placeholder in placeholders) {
            jsonArray.add(placeholder.serialize())
        }
        jsonObject.add("placeholders", jsonArray)

        return Messenger.encodeMessage(type, id, jsonObject)
    }

    companion object {
        fun decode(content: JsonElement?, id: UUID): SendResultMessageAction {
            checkNotNull(content) { "Missing content" }

            val receiver = elementAsString("receiver", content)
            val message = elementAsString("message", content)
            val placeholders = mutableListOf<Placeholder<String>>()

            elementAsJsonArray("placeholders", content).forEach {p ->
                PlaceholderSerializer.deserialize(p.asString)?.let {
                    placeholders.add(it)
                }
            }

            return SendResultMessageAction(id, receiver, message, placeholders)
        }
    }
}