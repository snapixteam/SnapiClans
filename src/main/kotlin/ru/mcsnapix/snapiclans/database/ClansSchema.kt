package ru.mcsnapix.snapiclans.database

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import ru.mcsnapix.snapiclans.api.Clan

data class ExposedClan(val name: String, val displayName: String, val owner: String)
object ClanCache {
    private val clans = mutableSetOf<Clan>()

    fun add(clan: Clan) {
        clans.add(clan)
    }

    fun get(predicate: (Clan) -> Boolean): Clan? {
        for (element in clans) if (predicate(element)) return element
        return null
    }

    fun reload(clan: Clan) {
        get { it.name == clan.name }?.let {
            remove(it)
        }
        add(clan)
    }

    fun remove(clan: Clan) {
        clans.remove(clan)
    }
}
object ClanService {
    private object Clans : Table() {
        val id = integer("id").autoIncrement()
        val name = varchar("name", length = 50).uniqueIndex()
        val displayName = varchar("display_name", length = 50)
        val owner = varchar("display_name", length = 50)

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(Databases.database) {
            SchemaUtils.create(Clans)
        }
    }

    suspend fun create(clan: ExposedClan): Int {
        val id = dbQuery {
            Clans.insert {
                it[name] = clan.name
                it[displayName] = clan.displayName
                it[owner] = clan.owner
            }[Clans.id]
        }

        ClanCache.add(Clan(id, clan))

        return id
    }

    suspend fun readAll(): List<Clan> {
        return dbQuery {
            Clans.selectAll().map { Clan(it[Clans.id], it[Clans.name], it[Clans.displayName], it[Clans.owner]) }
        }
    }

    suspend fun read(id: Int): Clan? {
        return dbQuery {
            Clans.select { Clans.id eq id }
                .map { Clan(it[Clans.id], it[Clans.name], it[Clans.displayName], it[Clans.owner]) }
                .singleOrNull()
        }
    }

    suspend fun read(name: String): Clan? {
        return dbQuery {
            Clans.select { Clans.name eq name }
                .map { Clan(it[Clans.id], it[Clans.name], it[Clans.displayName], it[Clans.owner]) }
                .singleOrNull()
        }
    }

    suspend fun update(id: Int, clan: ExposedClan) {
        dbQuery {
            Clans.update({ Clans.id eq id }) {
                it[name] = clan.name
                it[displayName] = clan.displayName
                it[owner] = clan.owner
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Clans.deleteWhere { Clans.id.eq(id) }
        }
    }

    suspend fun delete(name: String) {
        dbQuery {
            Clans.deleteWhere { Clans.name.eq(name) }
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}