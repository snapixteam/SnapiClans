package ru.mcsnapix.snapiclans.api.clans

import co.aikar.idb.DbRow
import ru.mcsnapix.snapiclans.database.UserDatabase

/**
 * The Clan class provides access to the properties of the clan.
 *
 * @author Flaimer
 * @since 0.0.1
 * @see ClanDatabase
 */
data class Clan(private val dbRow: DbRow) {
    /** The ID of the clan. */
    val id: Int = dbRow.getInt("id")

    /** The name of the clan. */
    val name: String = dbRow.getString("name")

    /** The display name of the clan. */
    val displayName: String = dbRow.getString("display_name")

    /** The name owner of the clan. */
    val owner: String = dbRow.getString("owner")

    /** The list of members in the clan. */
    val members: List<User> = UserDatabase.values(id).filterNotNull()
}