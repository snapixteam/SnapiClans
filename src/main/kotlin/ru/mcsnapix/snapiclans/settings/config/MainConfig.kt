package ru.mcsnapix.snapiclans.settings.config

import ru.mcsnapix.snapiclans.api.clans.ClanPermission
import ru.mcsnapix.snapiclans.api.clans.role.ClanRole
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger
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

        @ConfKey("create-command")
        @DefaultString("create")
        fun createCommand(): String

        @ConfKey("remove-command")
        @DefaultString("remove|delete|rem|del")
        fun removeCommand(): String
    }

    @SubSection
    fun roles(): Roles
    interface Roles {
        @ConfKey("default-role")
        @DefaultObject("defaultRoleDefault")
        fun defaultRole(): ClanRole

        @ConfKey("owner-role")
        @DefaultObject("ownerRoleDefault")
        fun ownerRole(): ClanRole

        @ConfKey("other-roles")
        @DefaultObject("otherRolesDefault")
        fun otherRoles(): List<ClanRole>

        companion object {
            @JvmStatic
            @Suppress("unused")
            fun defaultRoleDefault() = ClanRole("default", "Участник", 1, emptySet())

            @JvmStatic
            @Suppress("unused")
            fun ownerRoleDefault() = ClanRole("owner", "Владелец", 10, ClanPermission.values().toSet())

            @JvmStatic
            @Suppress("unused")
            fun otherRolesDefault() = listOf(
                ClanRole("veteran", "Ветеран", 2, setOf(ClanPermission.INVITE, ClanPermission.KICK)),
                ClanRole(
                    "vice",
                    "Заместитель",
                    5,
                    setOf(ClanPermission.INVITE, ClanPermission.KICK, ClanPermission.SET_ROLE)
                )
            )
        }
    }

    @SubSection
    fun regex(): Regex
    interface Regex {
        @ConfKey("clan-name")
        @DefaultString("[A-z0-9]{3,16}")
        fun clanName(): String

        @ConfKey("clan-display-name")
        @DefaultString("[A-zА-я0-9]{3,16})")
        fun clanDisplayName(): String
    }

    @SubSection
    fun economy(): Economy
    interface Economy {
        @ConfKey("create-clan")
        @DefaultInteger(1000)
        fun createClan(): Int
    }
}