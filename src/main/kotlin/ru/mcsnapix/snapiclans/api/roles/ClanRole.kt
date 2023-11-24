package ru.mcsnapix.snapiclans.api.roles

import ru.mcsnapix.snapiclans.settings.Settings

data class ClanRole(val name: String, val displayName: String, val weight: Int, val permissions: Set<ClanPermission>) {
    companion object {
        fun owner() = Settings.config.roles().ownerRole()
        fun default() = Settings.config.roles().defaultRole()
        fun other() = Settings.config.roles().otherRoles()

        fun role(name: String): ClanRole {
            return clanRoles()[name] ?: default()
        }

        private fun clanRoles(): Map<String, ClanRole> {
            val clanRoles = mutableMapOf<String, ClanRole>()
            owner().let {
                clanRoles[it.name] = it
            }
            default().let {
                clanRoles[it.name] = it
            }
            other().forEach { role -> clanRoles[role.name] = role }
            return clanRoles
        }
    }
}