package ru.mcsnapix.snapiclans.api.clans

import co.aikar.idb.DbRow
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.mcsnapix.snapiclans.caching.cache.ClanCaches
import ru.mcsnapix.snapiclans.managers.RoleManager

data class User(private val dbRow: DbRow) {
    val clanId: Int = dbRow.getInt("clan_id")
    val clan: Clan = ClanCaches[clanId] ?: throw IllegalArgumentException("Clan not found in database")
    val name: String = dbRow.getString("username")
    val player: Player?
        get() = Bukkit.getPlayer(name)
    val role: ClanRole =
        RoleManager.clanRoles[dbRow.getString("role")] ?: throw IllegalArgumentException("Role not found in config")
}