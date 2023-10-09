package ru.mcsnapix.plugin_name

import co.aikar.idb.DB
import co.aikar.idb.DatabaseOptions
import co.aikar.idb.PooledDatabaseOptions
import org.bukkit.plugin.java.JavaPlugin
import ru.mcsnapix.plugin_name.settings.Settings

class PLUGIN_NAME : JavaPlugin() {
    override fun onLoad() {
        instance = this
    }

    override fun onEnable() {
        Settings.enable()
        enableMySQL()
    }

    fun onReload() {
        Settings.reload()
    }

    override fun onDisable() {
        disableMySQL()
    }

    private fun enableMySQL() {
        val config = Settings.mysql
        val databaseOptions =
            DatabaseOptions.builder().mysql(config.username(), config.password(), config.database(), config.host())
                .build()
        val db = PooledDatabaseOptions.builder().options(databaseOptions).createHikariDatabase()
        DB.setGlobalDatabase(db)
    }

    private fun disableMySQL() {
        DB.close()
    }

    companion object {
        lateinit var instance: PLUGIN_NAME
    }
}