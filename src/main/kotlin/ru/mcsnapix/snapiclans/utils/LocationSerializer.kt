package ru.mcsnapix.snapiclans.utils

import org.bukkit.Bukkit
import org.bukkit.Location

object LocationSerializer {
    fun serialize(location: Location) =
        "${location.x};${location.y};${location.z};${location.world.name};${location.yaw};${location.pitch}"

    fun deserialize(string: String): Location? {
        val parts = string.split(";".toRegex())
        if (parts.size != 6) {
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
}