package ru.mcsnapix.snapiclans.database

import co.aikar.idb.DB
import co.aikar.idb.DbRow
import org.intellij.lang.annotations.Language
import ru.mcsnapix.snapiclans.api.clans.ClanRole
import ru.mcsnapix.snapiclans.api.clans.User

object UserDatabase {
    fun values(): List<User?> {
        val result = DB.getResults(SELECT_USERS) ?: return emptyList()
        return result.map { User(it) }
    }

    fun values(id: Int): List<User?> {
        val result = DB.getResults(SELECT_USERS_WITH_ID, id) ?: return emptyList()
        return result.map { User(it) }
    }

    fun values(nameClan: String): List<User?> {
        val result = DB.getResults(SELECT_USERS_WITH_CLAN_NAME, nameClan) ?: return emptyList()
        return result.map { User(it) }
    }

    operator fun get(name: String): User? {
        val result: DbRow = DB.getFirstRow(SELECT_USER_WITH_NAME, name) ?: return null
        return User(result)
    }

    fun add(id: Int, name: String, role: ClanRole) {
        DB.executeUpdate(CREATE_USER, id, name, role.name)
    }

    fun remove(name: String) {
        DB.executeUpdate(REMOVE_USER, name)
    }

    fun setRole(name: String, role: ClanRole) {
        DB.executeUpdate(SET_ROLE_USER, role.name, name)
    }

    @Language("SQL")
    private const val SELECT_USERS = "SELECT * FROM clan_members"

    @Language("SQL")
    private const val SELECT_USERS_WITH_ID = "SELECT * FROM clan_members WHERE clan_id = ?"

    @Language("SQL")
    private const val SELECT_USERS_WITH_CLAN_NAME =
        "SELECT cm.* FROM clan_members cm JOIN clan_clans c ON cm.clan_id = c.id WHERE c.name = ?"

    @Language("SQL")
    private const val SELECT_USER_WITH_NAME = "SELECT * FROM clan_members WHERE `username` = ?"

    @Language("SQL")
    private const val CREATE_USER = "INSERT IGNORE INTO clan_members(`clan_id`, `username`, `role`) VALUES (?, ?, ?)"

    @Language("SQL")
    private const val REMOVE_USER = "DELETE FROM clan_members WHERE `username` = ?"

    @Language("SQL")
    private const val SET_ROLE_USER = "UPDATE clan_members SET `role` = ? WHERE `username` = ?"
}