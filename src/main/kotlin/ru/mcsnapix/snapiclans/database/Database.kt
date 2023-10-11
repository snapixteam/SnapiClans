package ru.mcsnapix.snapiclans.database

import co.aikar.idb.DB
import co.aikar.idb.DatabaseOptions
import co.aikar.idb.PooledDatabaseOptions
import org.intellij.lang.annotations.Language
import ru.mcsnapix.snapiclans.Part
import ru.mcsnapix.snapiclans.api.clans.Clan
import ru.mcsnapix.snapiclans.api.clans.ClanUser
import ru.mcsnapix.snapiclans.settings.Settings

internal object Database : Part() {
    override fun enable() {
        val config = Settings.database
        val databaseOptions =
            DatabaseOptions.builder().mysql(config.username(), config.password(), config.database(), config.host())
                .build()
        val db = PooledDatabaseOptions.builder().options(databaseOptions).createHikariDatabase()
        DB.setGlobalDatabase(db)
        initialize()
    }

    override fun reload() {
        enable()
        disable()
    }

    override fun disable() {
        DB.close()
    }

    private fun initialize() {
        DB.executeUpdateAsync(CREATE_TABLE_CLANS).thenApply {
            DB.executeUpdate(CREATE_TABLE_MEMBERS)
        }
    }

    fun clans(): List<Clan> {
        val result = DB.getResults(SELECT_CLANS) ?: return emptyList()
        return result.map { Clan(it) }
    }

    fun clan(id: Int): Clan? {
        val result = DB.getFirstRow(SELECT_CLAN_WITH_ID, id) ?: return null
        return Clan(result)
    }

    fun clan(name: String): Clan? {
        val result = DB.getFirstRow(SELECT_CLAN_WITH_NAME, name) ?: return null
        return Clan(result)
    }

    fun createClan(name: String, displayName: String, owner: String) {
        DB.executeInsert(INSERT_CLAN, name, displayName, owner)
    }

    fun removeClan(id: Int) {
        DB.executeUpdate(REMOVE_CLAN, id)
    }

    fun userClan(username: String): Clan? {
        val result = DB.getFirstRow(SELECT_USER_CLAN, username) ?: return null
        return Clan(result)
    }

    fun users(): List<ClanUser> {
        val result = DB.getResults(SELECT_USERS) ?: return emptyList()
        return result.map { ClanUser(it) }
    }

    fun users(clanId: Int): List<ClanUser> {
        val result = DB.getResults(SELECT_USERS_WITH_ID, clanId) ?: return emptyList()
        return result.map { ClanUser(it) }
    }

    fun user(username: String): ClanUser? {
        val result = DB.getFirstRow(SELECT_USER_WITH_NAME, username) ?: return null
        return ClanUser(result)
    }

    fun createUser(clanId: Int, username: String, role: String) {
        DB.executeInsert(INSERT_USER, clanId, username, role)
    }

    fun removeUser(username: String) {
        DB.executeUpdate(REMOVE_USER, username)
    }

    fun setRoleUser(username: String, role: String) {
        DB.executeUpdate(SET_ROLE_USER, role, username)
    }

    @Language("SQL")
    private val CREATE_TABLE_CLANS = """
        CREATE TABLE IF NOT EXISTS `clans`
        (
            `id` INTEGER NOT NULL AUTO_INCREMENT,
            `name` VARCHAR(16) NOT NULL,
            `display_name` VARCHAR(16) NOT NULL,           
            `owner` VARCHAR(32) NOT NULL,
            UNIQUE(`name`, `owner`),
            PRIMARY KEY(`id`)
        )
    """.trimIndent()

    @Language("SQL")
    private val CREATE_TABLE_MEMBERS = """
        CREATE TABLE IF NOT EXISTS `members`
        (
            `clan_id` INTEGER NOT NULL,
            `username` VARCHAR(32) NOT NULL,
            `role` VARCHAR(32) NOT NULL,
            UNIQUE(`clan_id`, `username`),
            FOREIGN KEY (`clan_id`) REFERENCES `clans` (`id`) ON DELETE CASCADE,
            PRIMARY KEY(`username`)
        )
    """.trimIndent()

    @Language("SQL")
    private const val SELECT_CLANS = "SELECT * FROM clans"

    @Language("SQL")
    private const val SELECT_CLAN_WITH_ID = "SELECT * FROM clans WHERE `id` = ?"

    @Language("SQL")
    private const val SELECT_CLAN_WITH_NAME = "SELECT * FROM clans WHERE `name` = ?"

    @Language("SQL")
    private const val SELECT_USER_CLAN =
        "SELECT * FROM clans WHERE `id`=(SELECT `clan_id` FROM `members` WHERE `username` = ?)"

    @Language("SQL")
    private const val INSERT_CLAN = "INSERT IGNORE INTO clans(`name`, `display_name`, `owner`) VALUES (?, ?, ?)"

    @Language("SQL")
    private const val REMOVE_CLAN = "DELETE FROM clans WHERE `id` = ?"

    @Language("SQL")
    private const val SELECT_USERS = "SELECT * FROM members"

    @Language("SQL")
    private const val SELECT_USERS_WITH_ID = "SELECT * FROM members WHERE clan_id = ?"

    @Language("SQL")
    private const val SELECT_USER_WITH_NAME = "SELECT * FROM members WHERE `username` = ?"

    @Language("SQL")
    private const val INSERT_USER = "INSERT IGNORE INTO members(`clan_id`, `username`, `role`) VALUES (?, ?, ?)"

    @Language("SQL")
    private const val REMOVE_USER = "DELETE FROM members WHERE `username` = ?"

    @Language("SQL")
    private const val SET_ROLE_USER = "UPDATE members SET `role` = ? WHERE `username` = ?"
}