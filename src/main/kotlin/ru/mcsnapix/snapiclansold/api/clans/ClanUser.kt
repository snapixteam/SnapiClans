package ru.mcsnapix.snapiclansold.api.clans

import co.aikar.idb.DbRow
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.mcsnapix.snapiclansold.api.SnapiClansApi
import ru.mcsnapix.snapiclansold.api.clans.role.ClanRole
import ru.mcsnapix.snapiclansold.api.exceptions.RoleDontFoundInConfigException

data class ClanUser(private val dbRow: DbRow) {
    val clanId: Int = dbRow.getInt("clan_id")
    val clan: Clan get() = SnapiClansApi.clan(clanId)!!
    val name: String = dbRow.getString("username")
    val player: Player?
        get() = Bukkit.getPlayer(name)
    val role: ClanRole = RolesRegistry.clanRoles[dbRow.getString("role")] ?: throw RoleDontFoundInConfigException()
}
