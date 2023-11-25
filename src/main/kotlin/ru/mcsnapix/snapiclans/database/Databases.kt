package ru.mcsnapix.snapiclans.database

import org.intellij.lang.annotations.Language
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import ru.mcsnapix.snapiclans.settings.Settings
import java.sql.ResultSet

object Databases {
    private val config = Settings.database
    lateinit var database: Database

    fun enable() {
        database = Database.connect(
            url = "jdbc:mariadb://${config.host()}/${config.database()}",
            driver = "org.mariadb.jdbc.Driver",
            user = config.username(),
            password = config.password()
        )
        ClanService.enable()
        UserService.enable()
        executeUpdate(CREATE_TABLE_INVITE)
        executeUpdate(CREATE_TABLE_MESSENGER)
    }

    fun disable() {
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
}

//fun <T:Any> String.execAndMap(transform : (ResultSet) -> T) : List<T> {
//    val string = this
//    val result = arrayListOf<T>()
//    transaction(Databases.database) {
//        exec(string) { rs ->
//            while (rs.next()) {
//                result += transform(rs)
//            }
//        }
//    }
//    return result
//}

fun executeUpdate(query: String) {
    transaction(Databases.database) {
        exec(query)
    }
}

fun <T : Any> executeQuery(query: String, transform: (ResultSet) -> T): List<T> {
    val result = arrayListOf<T>()
    transaction(Databases.database) {
        exec(query) { rs ->
            while (rs.next()) {
                result += transform(rs)
            }
        }
    }
    return result
}