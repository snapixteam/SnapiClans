package ru.mcsnapix.snapiclans.caching.cache

import ru.mcsnapix.snapiclans.api.clans.User
import ru.mcsnapix.snapiclans.database.UserDatabase

object UserCaches {
    private val users = mutableMapOf<String, User>()

    operator fun get(name: String): User? {
        if (!users.containsKey(name)) {
            return null
        }

        return users[name]
    }

    fun add(name: String): Boolean {
        val result = UserDatabase[name] ?: return false
        users[name] = result
        return true
    }

    fun remove(name: String) {
        users.remove(name)
    }

    fun refresh(name: String) {
        remove(name)
        add(name)
    }

    fun enable() {
        UserDatabase.values().forEach {
            it?.let {user -> users[user.name] = user }
        }
    }

    fun disable() {
        users.clear()
    }
}