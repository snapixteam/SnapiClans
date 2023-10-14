package ru.mcsnapix.snapiclans.api.clans

import co.aikar.idb.DbRow
import ru.mcsnapix.snapiclans.database.UserDatabase

data class Clan(private val dbRow: DbRow) {
    val id: Int = dbRow.getInt("id")
    val name: String = dbRow.getString("name")
    val displayName: String = dbRow.getString("display_name")
    val owner: String = dbRow.getString("owner")
    val members: List<User> = UserDatabase.values(id).filterNotNull()
}