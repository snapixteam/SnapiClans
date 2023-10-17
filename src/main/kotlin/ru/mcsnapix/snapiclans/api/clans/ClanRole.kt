package ru.mcsnapix.snapiclans.api.clans

/**
 * The ClanRole class provides access to the properties of the role.
 *
 * @author Flaimer
 * @since 0.0.1
 */
data class ClanRole(val name: String, val displayName: String, val weight: Int, val permissions: Set<ClanPermission>)