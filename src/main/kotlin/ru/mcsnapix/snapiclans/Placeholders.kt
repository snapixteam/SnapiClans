package ru.mcsnapix.snapiclans

class Placeholder<out V>(val key: String, val value: V) {
    fun parse(string: String) = string.replace("%$key%", value.toString())
    fun serialize() = PlaceholderSerializer.serialize(this)
}

object PlaceholderSerializer {
    fun <V> serialize(placeholder: Placeholder<V>) = "${placeholder.key};${placeholder.value}"

    fun deserialize(string: String): Placeholder<String>? {
        val parts = string.split(";".toRegex())
        if (parts.size != 2) {
            return null
        }

        return Placeholder(parts[0], parts[1])
    }
}

object PlaceholderManager {
    fun <V> parse(string: String, list: List<Placeholder<V>>): String {
        var result = string

        for (placeholder in list) {
            result = placeholder.parse(result)
        }

        return result
    }
}