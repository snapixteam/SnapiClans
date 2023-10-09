package ru.mcsnapix.snapiclans

abstract class Part {
    abstract fun enable()
    open fun disable() {}
    open fun reload() {}
}