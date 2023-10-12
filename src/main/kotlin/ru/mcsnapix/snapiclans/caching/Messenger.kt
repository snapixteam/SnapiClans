package ru.mcsnapix.snapiclans.caching

import com.google.gson.*
import java.util.*

object Messenger {
    val gson: Gson = GsonBuilder().disableHtmlEscaping().create()











    fun encodeMessage(type: ActionType, id: UUID, content: JsonElement): String {
        val jsonObject = JsonObject()
        jsonObject.add("id", JsonPrimitive(id.toString()))
        jsonObject.add("type", JsonPrimitive(type.name))
        jsonObject.add("content", content)

        return gson.toJson(jsonObject)
    }
}