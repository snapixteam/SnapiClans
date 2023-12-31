package ru.mcsnapix.snapiclans.caching.cache

import ru.mcsnapix.snapiclans.api.clans.Clan
import ru.mcsnapix.snapiclans.database.ClanDatabase

object ClanCaches {
    private val clans = mutableMapOf<String, Clan>()

    operator fun get(name: String): Clan? {
        if (!clans.containsKey(name)) {
            return null
        }

        return clans[name]
    }

    operator fun get(id: Int): Clan? {
        val result = clans.values.find { it.id == id }
        return result
    }

    fun add(name: String): Boolean {
        val result = ClanDatabase[name] ?: return false
        clans[name] = result
        return true
    }

    fun remove(name: String) {
        clans.remove(name)
    }

    fun refresh(name: String) {
        remove(name)
        add(name)
    }


    fun enable() {
        ClanDatabase.values().filterNotNull().forEach {
            clans[it.name] = it
        }
    }

    fun disable() {
        clans.clear()
    }
}