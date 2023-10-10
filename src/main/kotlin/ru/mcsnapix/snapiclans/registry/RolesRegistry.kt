package ru.mcsnapix.snapiclans.registry

import ru.mcsnapix.snapiclans.Part
import ru.mcsnapix.snapiclans.api.clans.role.ClanRole
import ru.mcsnapix.snapiclans.settings.Settings

internal object RolesRegistry : Part() {
    val clanRoles = mutableMapOf<String, ClanRole>()

    override fun enable() {
        val rolesConfig = Settings.config.roles()

        val list = mutableListOf(rolesConfig.ownerRole(), rolesConfig.defaultRole())
        list.addAll(rolesConfig.otherRoles())

        for (role in list) {
            clanRoles[role.name] = role
        }
    }

    override fun reload() {
        disable()
        enable()
    }

    override fun disable() {
        clanRoles.clear()
    }
}