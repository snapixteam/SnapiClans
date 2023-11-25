package ru.mcsnapix.snapiclans.api

import ru.mcsnapix.snapiclans.database.UserCache

object ClansAPI {
    @JvmStatic
    fun getUserClan(name: String): Clan? {
        val user = UserCache.get { it.name == name } ?: return null
        return user.clan()
    }
}