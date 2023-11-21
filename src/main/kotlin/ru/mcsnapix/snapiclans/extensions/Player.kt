package ru.mcsnapix.snapiclans.extensions

import com.alessiodp.lastloginapi.api.LastLogin
import com.alessiodp.lastloginapi.api.interfaces.LastLoginPlayer
import net.kyori.adventure.text.minimessage.MiniMessage
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ru.mcsnapix.snapiclans.Placeholder
import ru.mcsnapix.snapiclans.PlaceholderManager
import ru.mcsnapix.snapiclans.SnapiClans


val mm = MiniMessage.miniMessage()
val api = LastLogin.getApi()

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

fun <V> Player.send(message: String, placeholders: List<Placeholder<V>>) {
    send(PlaceholderManager.parse(message, placeholders))
}

fun <V> Player.send(message: String, vararg placeholder: Placeholder<V>) {
    send(message, placeholder.toList())
}

fun Player.toLastLoginPlayer(): LastLoginPlayer {
    return api.getPlayer(uniqueId)
}

fun getLastLoginPlayer(name: String): LastLoginPlayer? {
    return api.getPlayerByName(name).first()
}