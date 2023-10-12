package ru.mcsnapix.snapiclans.api.clans

data class ClanRole(val name: String, val displayName: String, val weight: Int, val permissions: Set<ClanPermission>)