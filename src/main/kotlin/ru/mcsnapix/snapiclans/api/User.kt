package ru.mcsnapix.snapiclans.api

import ru.mcsnapix.snapiclans.api.roles.ClanRole
import ru.mcsnapix.snapiclans.database.ClanCache
import ru.mcsnapix.snapiclans.database.ExposedUser

data class User(val clanId: Int, val name: String, val role: ClanRole) {
    constructor(exposedUser: ExposedUser) : this(
        exposedUser.clanId,
        exposedUser.name,
        exposedUser.role
    )

    fun clan(): Clan? {
        return ClanCache.get { it.id == clanId }
    }
}