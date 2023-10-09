package ru.mcsnapix.snapiclans.settings

import ru.mcsnapix.snapiclans.Part
import ru.mcsnapix.snapiclans.settings.config.DatabaseConfig
import ru.mcsnapix.snapiclans.settings.config.MainConfig
import ru.mcsnapix.snapiclans.settings.serializers.ClanRoleSerializer
import space.arim.dazzleconf.ConfigurationOptions

object Settings : Part() {
    private lateinit var databaseConfig: Configuration<DatabaseConfig>
    val database = databaseConfig.data()
    private lateinit var mainConfig: Configuration<MainConfig>
    val config = mainConfig.data()

    override fun enable() {
        val options = ConfigurationOptions.Builder()
            .addSerialiser(ClanRoleSerializer())
            .setCreateSingleElementCollections(true)
            .build()
        databaseConfig = Configuration.create("database.yml", DatabaseConfig::class.java, options)
        mainConfig = Configuration.create("config.yml", MainConfig::class.java, options)
    }

    override fun reload() {
        databaseConfig.reloadConfig()
        mainConfig.reloadConfig()
    }
}