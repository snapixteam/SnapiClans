package ru.mcsnapix.snapiclansold.registry

import ru.mcsnapix.snapiclansold.registry.invite.InvitationRegistry

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