package ru.mcsnapix.snapiclans.api

import ru.mcsnapix.snapiclans.api.clans.Clan
import ru.mcsnapix.snapiclans.api.clans.ClanUser
import ru.mcsnapix.snapiclans.registry.ClansRegistry

object SnapiClansApi {
    fun clan(name: String): Clan {
        return ClansRegistry.get(name)
    }

    fun clanUser(name: String): ClanUser {
        TODO("Not implemented yet")
    }
}