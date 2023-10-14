package ru.mcsnapix.snapiclans.managers

object Manager {
    fun enable() {
        RoleManager.enable()
    }

    fun reload() {
        RoleManager.reload()
    }

    fun disable() {
        RoleManager.disable()
    }
}