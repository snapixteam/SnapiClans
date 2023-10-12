package ru.mcsnapix.snapiclans.extensions

import com.google.gson.*
import java.util.*

val Gson: Gson = GsonBuilder().disableHtmlEscaping().create()

fun encodeMessageAsString(type: String, id: UUID, content: JsonElement): String {
    val jsonObject = JsonObject()
    jsonObject.add("id", JsonPrimitive(id.toString()))
    jsonObject.add("type", JsonPrimitive(type))
    jsonObject.add("content", content)

    return Gson.toJson(jsonObject)
}