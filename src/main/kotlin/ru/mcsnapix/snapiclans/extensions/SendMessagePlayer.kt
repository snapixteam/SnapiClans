package ru.mcsnapix.snapiclans.extensions

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.minimessage.MiniMessage
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player
import ru.mcsnapix.snapiclans.SnapiClans

val mm = MiniMessage.miniMessage();

fun Player.send(message: String) {
    var result = message
    if (result.isEmpty()) {
        return
    }
    result = ChatColor.translateAlternateColorCodes('&', message)
    result = parsePlayer(result, this)
    
    if (result.contains("<mm>", true)) {
        result = result.replace("<mm>", "", true)
        val audience = SnapiClans.instance.adventure().player(this)
        audience.sendMessage(mm.deserialize(result))
        return
    }

    player.sendMessage(result)
}