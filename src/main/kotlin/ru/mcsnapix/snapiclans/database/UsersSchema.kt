package ru.mcsnapix.snapiclans.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import ru.mcsnapix.snapiclans.api.User
import ru.mcsnapix.snapiclans.api.roles.ClanRole
import ru.mcsnapix.snapiclans.messenger.Messenger
import ru.mcsnapix.snapiclans.messenger.actions.CreateUserAction
import ru.mcsnapix.snapiclans.messenger.actions.RemoveUserAction
import ru.mcsnapix.snapiclans.messenger.actions.UpdateUserAction
import java.util.*

data class ExposedUser(val clanId: Int, val name: String, val role: ClanRole)
object UserCache {
    private val users = mutableSetOf<User>()

    fun add(user: User) {
        users.add(user)
    }

    fun get(predicate: (User) -> Boolean): User? {
        for (element in users) if (predicate(element)) return element
        return null
    }

    fun getAll(predicate: (User) -> Boolean): List<User> {
        val result = mutableListOf<User>()
        for (element in users) {
            if (predicate(element)) result.add(element)
        }
        return result
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
    object Users : Table("clan_users") {
        val clanId = reference("clan_id", ClanService.Clans.id, ReferenceOption.CASCADE).uniqueIndex()
        val name = varchar("name", length = 50).uniqueIndex()
        val role = varchar("role", length = 50)

        override val primaryKey = PrimaryKey(name)
    }

    fun enable() {
        transaction(Databases.database) {
            SchemaUtils.create(Users)
            runBlocking {
                readAll().forEach {
                    UserCache.add(it)
                }
            }
        }
    }

    suspend fun create(exposedUser: ExposedUser): User {
        dbQuery {
            Users.insert {
                it[clanId] = exposedUser.clanId
                it[name] = exposedUser.name
                it[role] = exposedUser.role.name
            }[Users.name]
        }

        val user = User(exposedUser)
        Messenger.sendOutgoingMessage(CreateUserAction(UUID.randomUUID(), user.name))

        return user
    }

    suspend fun readAll(): List<User> {
        return dbQuery {
            Users.selectAll().map { User(it[Users.clanId], it[Users.name], ClanRole.role(it[Users.role])) }
        }
    }

    suspend fun read(name: String): User? {
        return dbQuery {
            Users.select { Users.name eq name }
                .map { User(it[Users.clanId], it[Users.name], ClanRole.role(it[Users.role])) }
                .singleOrNull()
        }
    }

    suspend fun update(user: ExposedUser) {
        dbQuery {
            Users.update({ Users.name eq user.name }) {
                it[role] = user.role.name
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