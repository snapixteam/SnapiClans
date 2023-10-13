package ru.mcsnapix.snapiclans.caching.cache

object Caches {
    fun enable() {
        ClanCaches.enable()
        UserCaches.enable()
    }

    fun disable() {
        ClanCaches.disable()
        UserCaches.disable()
    }
}