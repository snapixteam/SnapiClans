package ru.mcsnapix.snapiclans.extensions

import org.bukkit.entity.Player
import ru.mcsnapix.snapiclans.database.Database

fun parsePlayer(string: String, player: Player): String {
    var result = string
    result = result.replace("%player_name%", player.name)
    return result
}