package ru.mcsnapix.plugin_name.settings.config

import space.arim.dazzleconf.annote.ConfDefault.DefaultString

interface MySQLConfig {
    @DefaultString("localhost:3306")
    fun host(): String

    @DefaultString("server_global")
    fun database(): String

    @DefaultString("root")
    fun username(): String

    @DefaultString("root")
    fun password(): String
}