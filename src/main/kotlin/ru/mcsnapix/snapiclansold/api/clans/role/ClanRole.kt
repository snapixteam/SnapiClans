package ru.mcsnapix.snapiclansold.api.clans.role

import ru.mcsnapix.snapiclansold.api.clans.ClanPermission

data class ClanRole(val name: String, val displayName: String, val weight: Int, val permissions: Set<ClanPermission>)
