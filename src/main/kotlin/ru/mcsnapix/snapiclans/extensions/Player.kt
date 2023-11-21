package ru.mcsnapix.snapiclans.extensions

import net.kyori.adventure.text.minimessage.MiniMessage
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player
import ru.mcsnapix.snapiclans.SnapiClans

val mm = MiniMessage.miniMessage()

fun Player.send(message: String) {
    var result = message
    if (result.isEmpty()) {
        return
    }
    result = ChatColor.translateAlternateColorCodes('&', message)

    if (result.contains("<mm>")) {
        result = result.replace("<mm>", "")
        val audience = SnapiClans.instance.adventure().player(this)
        audience.sendMessage(mm.deserialize(result))
    } else {
        player.sendMessage(result)
    }
}