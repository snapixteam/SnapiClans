package ru.mcsnapix.snapiclans.database

import co.aikar.idb.DB
import co.aikar.idb.DatabaseOptions
import co.aikar.idb.PooledDatabaseOptions
import org.intellij.lang.annotations.Language
import org.jetbrains.exposed.sql.Database
import ru.mcsnapix.snapiclans.settings.Settings

object Databases {
    lateinit var database: Database
    val clansService: ClanService = ClanService

    fun enable() {
        val config = Settings.database
        database = Database.connect(
            url = "jdbc:mysql://${config.host()}/${config.database()}",
            user = config.username(),
            driver = "com.mysql.cj.jdbc.Driver",
            password = config.password()
        )

        // TODO: Перейти полностью на exposed
        val db = PooledDatabaseOptions.builder().options(
            DatabaseOptions.builder().mysql(config.username(), config.password(), config.database(), config.host())
                .build()
        ).createHikariDatabase()
        DB.setGlobalDatabase(db)
        db.executeUpdate(CREATE_TABLE_INVITE)
        db.executeUpdate(CREATE_TABLE_MESSENGER)
    }

    @Language("SQL")
    private val CREATE_TABLE_INVITE = """
        CREATE TABLE IF NOT EXISTS `clan_invite`
        (
            `id` INTEGER NOT NULL AUTO_INCREMENT,
            `clan_id` INTEGER NOT NULL,
            `sender` VARCHAR(32) NOT NULL,
            `receiver` VARCHAR(32) NOT NULL,
            `time` TIMESTAMP NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
            UNIQUE(`sender`, `receiver`),
            FOREIGN KEY (`clan_id`) REFERENCES `clan_clans` (`id`) ON DELETE CASCADE,
            PRIMARY KEY(`id`) USING BTREE
        )
    """.trimIndent()

    @Language("SQL")
    private val CREATE_TABLE_MESSENGER = """
        CREATE TABLE IF NOT EXISTS `clan_messenger`
        (
            `id` INTEGER NOT NULL AUTO_INCREMENT,
            `time` TIMESTAMP NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
            `msg` TEXT NOT NULL COLLATE 'utf8mb4_general_ci',
            PRIMARY KEY(`id`) USING BTREE
        )
    """.trimIndent()
//    fun enable() {
//        val config = Settings.database
//        val databaseOptions =
//            DatabaseOptions.builder().mysql(config.username(), config.password(), config.database(), config.host())
//                .build()
//        val db = PooledDatabaseOptions.builder().options(databaseOptions).createHikariDatabase()
//        DB.setGlobalDatabase(db)
//        initialize()
//    }
//
//    fun disable() {
//        DB.close()
//    }
//
//    private fun initialize() {
//        DB.executeUpdate(CREATE_TABLE_MEMBERS)
//        DB.executeUpdate(CREATE_TABLE_INVITE)
//        DB.executeUpdate(CREATE_TABLE_MESSENGER)
//    }
//
//    @Language("SQL")
//    private val CREATE_TABLE_MEMBERS = """
//        CREATE TABLE IF NOT EXISTS `clan_members`
//        (
//            `clan_id` INTEGER NOT NULL,
//            `username` VARCHAR(32) NOT NULL,
//            `role` VARCHAR(32) NOT NULL,
//            UNIQUE(`clan_id`, `username`),
//            FOREIGN KEY (`clan_id`) REFERENCES `clan_clans` (`id`) ON DELETE CASCADE,
//            PRIMARY KEY(`username`)
//        )
//    """.trimIndent()
}