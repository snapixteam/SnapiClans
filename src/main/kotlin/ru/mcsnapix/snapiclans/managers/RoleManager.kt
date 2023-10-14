package ru.mcsnapix.snapiclans.managers

import ru.mcsnapix.snapiclans.api.clans.ClanRole
import ru.mcsnapix.snapiclans.settings.Settings

object RoleManager {
    val clanRoles = mutableMapOf<String, ClanRole>()
    lateinit var owner: ClanRole
    lateinit var default: ClanRole
    lateinit var other: List<ClanRole>

    fun enable() {
        val rolesConfig = Settings.config.roles()

        owner = rolesConfig.ownerRole()
        default = rolesConfig.defaultRole()
        other = rolesConfig.otherRoles()

        clanRoles[owner.name] = owner
        clanRoles[default.name] = default
        other.forEach { role -> clanRoles[role.name] = role }
    }

    fun reload() {
        disable()
        enable()
    }

    fun disable() {
        clanRoles.clear()
    }
}