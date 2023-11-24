package ru.mcsnapix.snapiclans.api

import ru.mcsnapix.snapiclans.database.ExposedClan


data class Clan(val id: Int, val name: String, val displayName: String, val owner: String) {
    constructor(id: Int, exposedClan: ExposedClan) : this(id, exposedClan.name, exposedClan.displayName, exposedClan.owner)
}