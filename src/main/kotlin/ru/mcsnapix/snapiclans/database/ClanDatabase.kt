package ru.mcsnapix.snapiclans.database

import co.aikar.idb.DB
import co.aikar.idb.DbRow
import org.intellij.lang.annotations.Language
import ru.mcsnapix.snapiclans.api.clans.Clan
import ru.mcsnapix.snapiclans.api.clans.User

object ClanDatabase {
    fun values(): List<Clan?> {
        val result = DB.getResults(SELECT_CLANS) ?: return emptyList()
        return result.map { Clan(it) }
    }

    operator fun get(id: Int): Clan? {
        val result: DbRow = DB.getFirstRow(SELECT_CLAN_WITH_ID, id) ?: return null
        return Clan(result)
    }

    operator fun get(name: String): Clan? {
        val result: DbRow = DB.getFirstRow(SELECT_CLAN_WITH_NAME, name) ?: return null
        return Clan(result)
    }

    operator fun get(user: User): Clan? {
        val result: DbRow = DB.getFirstRow(SELECT_CLAN_WITH_USER, user.name) ?: return null
        return Clan(result)
    }

    fun changeDisplayName(name: String, displayName: String) {
        DB.executeUpdate(CHANGE_CLAN_DISPLAY_NAME, displayName, name)
    }

    fun add(name: String, displayName: String, owner: String) {
        DB.executeInsert(CREATE_CLAN, name, displayName, owner)
    }

    fun remove(name: String) {
        DB.executeUpdate(REMOVE_CLAN, name)
    }

    @Language("SQL")
    private const val SELECT_CLANS = "SELECT * FROM clan_clans"

    @Language("SQL")
    private const val SELECT_CLAN_WITH_ID = "SELECT * FROM clan_clans WHERE `id` = ?"

    @Language("SQL")
    private const val SELECT_CLAN_WITH_NAME = "SELECT * FROM clan_clans WHERE `name` = ?"

    @Language("SQL")
    private const val SELECT_CLAN_WITH_USER =
        "SELECT * FROM clan_clans WHERE `id`=(SELECT `clan_id` FROM `clan_members` WHERE `username` = ?)"

    @Language("SQL")
    private const val CHANGE_CLAN_DISPLAY_NAME = "UPDATE clan_clans SET `display_name` = ? WHERE `name` = ?"

    @Language("SQL")
    private const val CREATE_CLAN = "INSERT IGNORE INTO clan_clans(`name`, `display_name`, `owner`) VALUES (?, ?, ?)"

    @Language("SQL")
    private const val REMOVE_CLAN = "DELETE FROM clan_clans WHERE `id` = ?"
}