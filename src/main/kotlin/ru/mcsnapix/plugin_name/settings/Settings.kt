package ru.mcsnapix.plugin_name.settings

import ru.mcsnapix.plugin_name.settings.config.MySQLConfig
import space.arim.dazzleconf.ConfigurationOptions

object Settings {
    private lateinit var mysqlConfig: Configuration<MySQLConfig>
    val mysql = mysqlConfig.data()

    fun enable() {
        val options = ConfigurationOptions.Builder()
            .setCreateSingleElementCollections(true)
            .build()
        mysqlConfig = Configuration.create("mysql.yml", MySQLConfig::class.java, options)
    }

    fun reload() {
        mysqlConfig.reloadConfig()
    }
}