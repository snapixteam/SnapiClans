package ru.mcsnapix.snapiclans.api

data class User(val id: Int, val name: String, val displayName: String, val owner: String) {
    constructor(id: Int, exposedClan: ExposedClan) : this(id, exposedClan.name, exposedClan.displayName, exposedClan.owner)
}