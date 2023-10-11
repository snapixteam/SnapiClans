package ru.mcsnapix.snapiclans.extensions

import net.milkbowl.vault.economy.Economy
import org.bukkit.entity.Player
import ru.mcsnapix.snapiclans.SnapiClans

val economy: Economy get() = SnapiClans.instance.economy

fun Player.hasMoney(amount: Int): Boolean = economy.getBalance(this) >= amount
fun Player.withdrawMoney(amount: Int) {
    economy.withdrawPlayer(this, amount.toDouble())
}