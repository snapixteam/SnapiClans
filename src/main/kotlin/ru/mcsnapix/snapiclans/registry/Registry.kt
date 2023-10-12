package ru.mcsnapix.snapiclans.registry

import ru.mcsnapix.snapiclans.Part
import ru.mcsnapix.snapiclans.registry.invite.InvitationRegistry

object Registry : Part() {
    override fun enable() {
        RolesRegistry.enable()
        InvitationRegistry.enable()
    }

    override fun reload() {
        RolesRegistry.reload()
    }

    override fun disable() {
        RolesRegistry.disable()
        InvitationRegistry.disable()
    }
}