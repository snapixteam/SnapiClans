package ru.mcsnapix.snapiclans.api

import ru.mcsnapix.snapiclans.api.clans.Clan
import ru.mcsnapix.snapiclans.api.clans.ClanUser
import ru.mcsnapix.snapiclans.database.Database
import ru.mcsnapix.snapiclans.registry.ClansRegistry

object SnapiClansApi {
    fun clan(name: String): Clan {
        return ClansRegistry.get(name)
    }

    fun createClan(name: String, displayName: String, owner: String) {
        Database.createClan(name, displayName, owner)
        ClansRegistry.update(name)
//        TODO("Add check correctly")
    }

//    fun clanUser(name: String): ClanUser {
////        TODO("Not implemented yet")
//    }
}