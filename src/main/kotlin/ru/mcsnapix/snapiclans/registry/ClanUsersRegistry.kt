package ru.mcsnapix.snapiclans.registry

import ru.mcsnapix.snapiclans.api.clans.ClanUser
import ru.mcsnapix.snapiclans.database.Database

internal object ClanUsersRegistry {
    private val users = mutableMapOf<String, ClanUser>()

    fun get(name: String): ClanUser? {
        if (!users.containsKey(name)) {
            if (!add(name)) {
                return null
            }
        }

        return users[name]
    }

    fun add(name: String): Boolean {
        val result = Database.user(name) ?: return false
        users[name] = result
        return true
    }

    fun update(name: String) {
        remove(name)
        add(name)
    }

    fun remove(name: String) {
        users.remove(name)
    }
}