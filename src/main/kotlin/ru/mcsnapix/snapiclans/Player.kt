package ru.mcsnapix.snapiclans

import com.alessiodp.lastloginapi.api.LastLogin
import com.alessiodp.lastloginapi.api.interfaces.LastLoginAPI
import com.alessiodp.lastloginapi.api.interfaces.LastLoginPlayer
import net.kyori.adventure.text.minimessage.MiniMessage
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.user.User
import net.md_5.bungee.api.ChatColor
import net.milkbowl.vault.economy.Economy
import org.bukkit.entity.Player


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

val economy: Economy
    get() = SnapiClans.instance.economy

fun Player.hasMoney(amount: Int): Boolean = economy.getBalance(this) >= amount
fun Player.withdrawMoney(amount: Int) {
    economy.withdrawPlayer(this, amount.toDouble())
}


fun getLastLoginPlayer(name: String): LastLoginPlayer? {
    return lastLoginApi.getPlayerByName(name).firstOrNull()
}

fun getLuckPermsUser(name: String): User? {
    return luckPerms.userManager.getUser(name)
}

fun LastLoginPlayer.isOffline(): Boolean {
    val user = getLuckPermsUser(name) ?: return true
    return !user.isOnline()
}

fun User.isOnline() = luckPerms.userManager.loadedUsers.contains(this)