package ru.mcsnapix.snapiclans.extensions

import org.bukkit.Bukkit
import org.bukkit.event.Event

fun callEvent(event: Event) = Bukkit.getPluginManager().callEvent(event)