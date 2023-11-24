package ru.mcsnapix.snapiclans.messenger.actions

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.bukkit.Bukkit
import ru.mcsnapix.snapiclans.Placeholder
import ru.mcsnapix.snapiclans.PlaceholderSerializer
import ru.mcsnapix.snapiclans.caching.Action
import ru.mcsnapix.snapiclans.caching.ActionType
import ru.mcsnapix.snapiclans.caching.Messenger
import ru.mcsnapix.snapiclans.extensions.send
import java.util.*

class SendMessageAction(
    id: UUID,
    val receiver: String,
    val message: String,
    val placeholders: List<Placeholder<String>>
) : Action(id) {
    constructor(id: UUID, receiver: String, message: String, vararg placeholders: Placeholder<String>) : this(
        id,
        receiver,
        message,
        placeholders.toList()
    )

    override val type: ActionType = ActionType.SEND_RESULT_MESSAGE

    override fun executeIncomingMessage() {
        val player = Bukkit.getPlayer(receiver) ?: return
        player.send(message, placeholders)
    }

    override fun encode(): String {
        val jsonObject = JsonObject()

        jsonObject.add("receiver", JsonPrimitive(receiver))
        jsonObject.add("message", JsonPrimitive(message))

        val jsonArray = JsonArray()
        for (placeholder in placeholders) {
            jsonArray.add(PlaceholderSerializer.serialize(placeholder))
        }
        jsonObject.add("placeholders", jsonArray)

        return Messenger.encodeMessage(type, id, jsonObject)
    }

    companion object {
        fun decode(content: JsonElement?, id: UUID): SendMessageAction {
            checkNotNull(content) { "Missing content" }

            val receiver = elementAsString("receiver", content)
            val message = elementAsString("message", content)
            val placeholders = mutableListOf<Placeholder<String>>()

            elementAsJsonArray("placeholders", content).forEach { p ->
                PlaceholderSerializer.deserialize(p.asString)?.let {
                    placeholders.add(it)
                }
            }

            return SendMessageAction(id, receiver, message, placeholders)
        }
    }
}