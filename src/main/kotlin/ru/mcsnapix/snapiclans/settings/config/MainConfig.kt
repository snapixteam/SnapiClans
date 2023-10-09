package ru.mcsnapix.snapiclans.settings.config

import ru.mcsnapix.snapiclans.api.clans.ClanPermission
import ru.mcsnapix.snapiclans.api.clans.ClanRole
import space.arim.dazzleconf.annote.ConfDefault.DefaultObject
import space.arim.dazzleconf.annote.ConfDefault.DefaultString
import space.arim.dazzleconf.annote.ConfKey
import space.arim.dazzleconf.annote.SubSection


interface MainConfig {
    @SubSection
    fun alias(): Alias
    interface Alias {
        @ConfKey("main-command")
        @DefaultString("clan|clans|guilds|guild")
        fun mainCommand(): String
    }

    @SubSection
    fun roles(): Roles
    interface Roles {
        @ConfKey("default-role")
        @DefaultObject("defaultRoleDefault")
        fun defaultRole(): ClanRole
        @Suppress("unused")
        fun defaultRoleDefault() = ClanRole("default", "Участник", 1, emptySet())

        @ConfKey("owner-role")
        @DefaultObject("ownerRoleDefault")
        fun ownerRole(): ClanRole
        @Suppress("unused")
        fun ownerRoleDefault() = ClanRole("owner", "Владелец", 10, ClanPermission.values().toSet())

        @ConfKey("other-roles")
        @DefaultObject("otherRolesDefault")
        fun otherRoles(): List<ClanRole>
        @Suppress("unused")
        fun otherRolesDefault() = listOf(
            ClanRole("veteran", "Ветеран", 2, setOf(ClanPermission.INVITE, ClanPermission.KICK)),
            ClanRole("vice", "Заместитель", 5, setOf(ClanPermission.INVITE, ClanPermission.KICK, ClanPermission.SET_ROLE))
        )
    }
}