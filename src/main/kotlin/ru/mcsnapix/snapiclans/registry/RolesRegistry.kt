package ru.mcsnapix.snapiclans.registry

import ru.mcsnapix.snapiclans.Part
import ru.mcsnapix.snapiclans.api.clans.role.ClanRole
import ru.mcsnapix.snapiclans.settings.Settings

internal object RolesRegistry : Part() {
    val clanRoles = mutableMapOf<String, ClanRole>()
    lateinit var owner: ClanRole
    lateinit var default: ClanRole
    lateinit var other: List<ClanRole>


    override fun enable() {
        val rolesConfig = Settings.config.roles()

        owner = rolesConfig.ownerRole()
        default = rolesConfig.defaultRole()
        other = rolesConfig.otherRoles()

        clanRoles[owner.name] = owner
        clanRoles[default.name] = default
        other.forEach { r -> clanRoles[r.name] = r }
    }

    override fun reload() {
        disable()
        enable()
    }

    override fun disable() {
        clanRoles.clear()
    }
}