package ru.mcsnapix.snapiclans.registry

import ru.mcsnapix.snapiclans.api.clans.Clan
import ru.mcsnapix.snapiclans.api.exceptions.ClanNullException
import ru.mcsnapix.snapiclans.database.Database

internal object ClansRegistry {
    private val clans = mutableMapOf<String, Clan>()

    fun get(name: String): Clan {
        if (!clans.containsKey(name)) {
            update(name)
        }

        return clans[name] ?: throw ClanNullException()
    }

    fun update(name: String) {
        clans[name] = Database.clan(name)
    }

    fun remove(name: String) {
        clans.remove(name)
    }
}