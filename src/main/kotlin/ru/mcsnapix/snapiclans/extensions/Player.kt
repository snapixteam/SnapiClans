package ru.mcsnapix.snapiclans.extensions

import com.alessiodp.lastloginapi.api.LastLogin
import com.alessiodp.lastloginapi.api.interfaces.LastLoginAPI
import com.alessiodp.lastloginapi.api.interfaces.LastLoginPlayer
import net.kyori.adventure.text.minimessage.MiniMessage
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.user.User
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player
import ru.mcsnapix.snapiclans.Placeholder
import ru.mcsnapix.snapiclans.PlaceholderManager
import ru.mcsnapix.snapiclans.SnapiClans


val mm = MiniMessage.miniMessage()
val lastLoginApi: LastLoginAPI = LastLogin.getApi()
val luckPerms = LuckPermsProvider.get()

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

fun getLastLoginPlayer(name: String): LastLoginPlayer? {
    val set = lastLoginApi.getPlayerByName(name)
    if (set.isEmpty()) {
        return null
    }
    return set.first()
}

fun getLuckPermsUser(name: String): User? {
    return luckPerms.userManager.getUser(name)
}

fun User.isOnline() = luckPerms.userManager.loadedUsers.contains(this)