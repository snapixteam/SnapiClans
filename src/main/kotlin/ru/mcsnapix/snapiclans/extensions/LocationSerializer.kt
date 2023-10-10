package ru.mcsnapix.snapiclans.extensions

import org.bukkit.Bukkit
import org.bukkit.Location

val Location.string: String
    get() = "${this.x};${this.y};${this.z};${this.world.name};${this.yaw};${this.pitch}"


val String.location: Location?
    get() {
        val parts = this.split(";".toRegex())
        if (parts.isEmpty()) {
            return null
        }
        val x = parts[0].toDouble()
        val y = parts[1].toDouble()
        val z = parts[2].toDouble()
        val world = Bukkit.getWorld(parts[3])
        val yaw = parts[4].toFloat()
        val pitch = parts[5].toFloat()
        return Location(world, x, y, z, yaw, pitch)
    }