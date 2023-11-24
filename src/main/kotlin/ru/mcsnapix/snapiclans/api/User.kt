package ru.mcsnapix.snapiclans.api

import ru.mcsnapix.snapiclans.database.ExposedUser

data class User(val clanId: Int, val name: String, val role: String) {
    constructor(exposedUser: ExposedUser) : this(
        exposedUser.clanId,
        exposedUser.name,
        exposedUser.role
    )
}