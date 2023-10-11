package ru.mcsnapix.snapiclans.api.clans

import co.aikar.idb.DbRow
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.mcsnapix.snapiclans.api.SnapiClansApi
import ru.mcsnapix.snapiclans.api.clans.role.ClanRole
import ru.mcsnapix.snapiclans.api.exceptions.RoleDontFoundInConfigException
import ru.mcsnapix.snapiclans.registry.RolesRegistry

data class ClanUser(private val dbRow: DbRow) {
    val clanId: Int = dbRow.getInt("clan_id")
    val clan: Clan get() = SnapiClansApi.clan(clanId)!!
    val username: String = dbRow.getString("username")
    val player: Player?
        get() = Bukkit.getPlayer(username)
    val role: ClanRole = RolesRegistry.clanRoles[dbRow.getString("role")] ?: throw RoleDontFoundInConfigException()
}
