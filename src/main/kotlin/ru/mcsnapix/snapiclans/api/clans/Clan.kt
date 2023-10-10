package ru.mcsnapix.snapiclans.api.clans

import co.aikar.idb.DbRow
import ru.mcsnapix.snapiclans.database.Database

data class Clan(private val dbRow: DbRow) {
    val id = dbRow.getInt("id")
    val name = dbRow.getString("name")
    val displayName = dbRow.getString("display_name")
    val owner = dbRow.getString("owner")
    val members: List<ClanUser> = Database.users(id)
}