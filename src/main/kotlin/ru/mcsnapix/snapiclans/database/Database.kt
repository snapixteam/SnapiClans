package ru.mcsnapix.snapiclans.database

import co.aikar.idb.DB
import co.aikar.idb.DatabaseOptions
import co.aikar.idb.PooledDatabaseOptions
import org.intellij.lang.annotations.Language
import ru.mcsnapix.snapiclans.Part
import ru.mcsnapix.snapiclans.settings.Settings

object Database : Part() {
    override fun enable() {
        val config = Settings.database
        val databaseOptions =
            DatabaseOptions.builder().mysql(config.username(), config.password(), config.database(), config.host())
                .build()
        val db = PooledDatabaseOptions.builder().options(databaseOptions).createHikariDatabase()
        DB.setGlobalDatabase(db)
        initialize();
    }

    private fun initialize() {
        DB.executeUpdateAsync(CREATE_TABLE_CLANS)
        DB.executeUpdateAsync(CREATE_TABLE_MEMBERS)
    }

    override fun reload() {
        enable()
        disable()
    }

    override fun disable() {
        DB.close()
    }

    @Language("SQL") private val CREATE_TABLE_CLANS = """
        CREATE TABLE IF NOT EXISTS `clans`
        (
            `id` INTEGER NOT NULL AUTO_INCREMENT,
            `name` VARCHAR(16) NOT NULL,
            `display_name` VARCHAR(32) NOT NULL,           
            `owner` VARCHAR(32) NOT NULL,
            UNIQUE(`name`, `owner`),
            PRIMARY KEY(`id`)
        )
    """.trimIndent()

    @Language("SQL") private val CREATE_TABLE_MEMBERS = """
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

    @Language("SQL") val SELECT_CLANS = "SELECT * FROM clans"
    @Language("SQL") val SELECT_CLAN_WITH_ID = "SELECT * FROM clans WHERE `id` = ?"
    @Language("SQL") val SELECT_CLAN_WITH_NAME = "SELECT * FROM clans WHERE `name` = ?"
    @Language("SQL") val SELECT_USER_CLAN = "SELECT * FROM clans WHERE `id`=(SELECT `clan_id` FROM `members` WHERE `username` = ?)"
    @Language("SQL") val INSERT_CLAN = "INSERT IGNORE INTO clans(`name`, `display_name`, `owner`) VALUES (?, ?, ?)"
    @Language("SQL") val REMOVE_CLAN = "DELETE FROM clans WHERE `id` = ?"
    @Language("SQL") val SELECT_USER = "SELECT * FROM members WHERE `username` = ?"
    @Language("SQL") val INSERT_USER = "INSERT IGNORE INTO members(`clan_id`, `username`, `role`) VALUES (?, ?, ?)"
//    @Language("SQL") val REMOVE_USER = TODO("Not yet implemented")
//    @Language("SQL") val SET_ROLE_USER = TODO("Not yet implemented")
}