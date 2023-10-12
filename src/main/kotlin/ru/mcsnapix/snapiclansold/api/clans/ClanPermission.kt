package ru.mcsnapix.snapiclansold.api.clans

enum class ClanPermission(val value: String) {
    INVITE("invite"),
    KICK("kick"),
    DISBAND("disband"),
    SET_ROLE("set_role")
}
