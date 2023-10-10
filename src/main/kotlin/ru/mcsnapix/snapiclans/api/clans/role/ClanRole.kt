package ru.mcsnapix.snapiclans.api.clans.role

import ru.mcsnapix.snapiclans.api.clans.ClanPermission

data class ClanRole(val name: String, val displayName: String, val weight: Int, val permissions: Set<ClanPermission>)
