package ru.mcsnapix.snapiclans.registry

import ru.mcsnapix.snapiclans.api.clans.Clan
import ru.mcsnapix.snapiclans.database.Database

internal object ClansRegistry {
    private val clans = mutableMapOf<String, Clan>()

    fun get(name: String): Clan? {
        if (!clans.containsKey(name)) {
            if (!add(name)) {
                return null
            }
        }

        return clans[name]
    }

    fun name(id: Int): Clan? {
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
        clans.remove(clan.name)
    }
}