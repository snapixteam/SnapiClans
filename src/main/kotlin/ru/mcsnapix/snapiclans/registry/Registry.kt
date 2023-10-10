package ru.mcsnapix.snapiclans.registry

import ru.mcsnapix.snapiclans.Part

object Registry : Part() {
    override fun enable() {
        RolesRegistry.enable()
    }

    override fun reload() {
        RolesRegistry.reload()
    }

    override fun disable() {
        RolesRegistry.disable()
    }
}