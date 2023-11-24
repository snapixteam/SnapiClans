package ru.mcsnapix.snapiclans.database

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import ru.mcsnapix.snapiclans.api.User
import ru.mcsnapix.snapiclans.messenger.Messenger
import ru.mcsnapix.snapiclans.messenger.actions.CreateUserAction
import ru.mcsnapix.snapiclans.messenger.actions.RemoveUserAction
import ru.mcsnapix.snapiclans.messenger.actions.UpdateUserAction
import java.util.*

data class ExposedUser(val clanId: Int, val name: String, val role: String)
object UserCache {
    private val users = mutableSetOf<User>()

    fun add(user: User) {
        users.add(user)
    }

    fun get(predicate: (User) -> Boolean): User? {
        for (element in users) if (predicate(element)) return element
        return null
    }

    fun reload(user: User) {
        get { it.name == user.name }?.let {
            remove(it)
        }
        add(user)
    }

    fun remove(user: User) {
        users.remove(user)
    }
}

object UserService {
    object Users : Table() {
        val clanId = reference("clan_id", ClanService.Clans.id, ReferenceOption.CASCADE).uniqueIndex()
        val name = varchar("name", length = 50).uniqueIndex()
        val role = varchar("role", length = 50)

        override val tableName = "clan_users"
        override val primaryKey = PrimaryKey(name)
    }

    init {
        transaction(Databases.database) {
            SchemaUtils.create(Users)
        }
    }

    suspend fun create(exposedUser: ExposedUser): Int {
        val id = dbQuery {
            Users.insert {
                it[clanId] = exposedUser.clanId
                it[name] = exposedUser.name
                it[role] = exposedUser.role
            }[Users.clanId]
        }

        val user = User(exposedUser)
        Messenger.sendOutgoingMessage(CreateUserAction(UUID.randomUUID(), user.name))

        return id
    }

    suspend fun readAll(): List<User> {
        return dbQuery {
            Users.selectAll().map { User(it[Users.clanId], it[Users.name], it[Users.role]) }
        }
    }

    suspend fun read(name: String): User? {
        return dbQuery {
            Users.select { Users.name eq name }
                .map { User(it[Users.clanId], it[Users.name], it[Users.role]) }
                .singleOrNull()
        }
    }

    suspend fun update(user: ExposedUser) {
        dbQuery {
            Users.update({ Users.name eq user.name }) {
                it[role] = user.role
            }
        }

        Messenger.sendOutgoingMessage(UpdateUserAction(UUID.randomUUID(), user.name))
    }

    suspend fun delete(name: String) {
        dbQuery {
            Users.deleteWhere { Users.name eq name }
        }

        Messenger.sendOutgoingMessage(RemoveUserAction(UUID.randomUUID(), name))
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}