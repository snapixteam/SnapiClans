package ru.mcsnapix.snapiclans.extensions

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import java.util.*

val Gson = GsonBuilder().disableHtmlEscaping().create()

fun encodeMessageAsString(type: String, id: UUID, content: JsonElement): String {
    val jsonObject = JsonObject()
    jsonObject.add("id", JsonPrimitive(id.toString()))
    jsonObject.add("type", JsonPrimitive(type))
    jsonObject.add("content", content)

    return Gson.toJson(jsonObject)
}