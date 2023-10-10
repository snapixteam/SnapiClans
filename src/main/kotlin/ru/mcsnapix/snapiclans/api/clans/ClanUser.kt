package ru.mcsnapix.snapiclans.api.clans

import co.aikar.idb.DbRow
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.mcsnapix.snapiclans.api.exceptions.RoleDontFoundInConfigException
import ru.mcsnapix.snapiclans.registry.RolesRegistry

data class ClanUser(private val dbRow: DbRow) {
    val clanId = dbRow.getInt("clan_id")
    val username = dbRow.getString("username")
    val player: Player?
        get() = Bukkit.getPlayer(username)
    val role = RolesRegistry.clanRoles[dbRow.getString("role")] ?: throw RoleDontFoundInConfigException()
}
