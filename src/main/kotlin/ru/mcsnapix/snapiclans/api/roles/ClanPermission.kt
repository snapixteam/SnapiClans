package ru.mcsnapix.snapiclans.api.roles

enum class ClanPermission(val name: String) {
    INVITE("invite"),
    KICK("kick"),
    DISBAND("disband"),
    SET_ROLE("set_role")
}