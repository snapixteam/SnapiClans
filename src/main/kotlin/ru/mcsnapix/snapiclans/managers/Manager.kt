package ru.mcsnapix.snapiclans.managers

import ru.mcsnapix.snapiclans.managers.invite.InviteManager

object Manager {
    fun enable() {
        RoleManager.enable()
        InviteManager.enable()
    }

    fun reload() {
        RoleManager.reload()
    }

    fun disable() {
        RoleManager.disable()
        InviteManager.disable()
    }
}