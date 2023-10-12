package ru.mcsnapix.snapiclansold.registry

import ru.mcsnapix.snapiclansold.api.clans.Clan
import ru.mcsnapix.snapiclansold.database.Database

internal object ClansRegistry {
    private val clans = mutableMapOf<String, Clan>()

    operator fun get(name: String): Clan? {
        if (!clans.containsKey(name)) {
            if (!add(name)) {
                return null
            }
        }

        return clans[name]
    }

    operator fun get(id: Int): Clan? {
        for (entry in clans.entries) {
            if (entry.value.id == id) {
                return entry.value
            }
        }
        Database.clan(id)?.let {
            add(it)
            return it
        }
        return null
    }

    fun add(name: String): Boolean {
        val result = Database.clan(name) ?: return false
        clans[name] = result
        return true
    }

    fun list() = clans

    fun add(clan: Clan): Boolean {
        clans[clan.name] = clan
        return true
    }

    fun update(name: String) {
        remove(name)
        add(name)
    }

    fun update(clan: Clan) {
        remove(clan)
        add(clan)
    }

    fun remove(name: String) {
        clans.remove(name)
    }

    fun remove(clan: Clan) {
        remove(clan.name)
    }
}