package ru.mcsnapix.snapiclans.settings

import ru.mcsnapix.snapiclans.settings.config.DatabaseConfig
import ru.mcsnapix.snapiclans.settings.config.MainConfig
import ru.mcsnapix.snapiclans.settings.config.MessageConfig
import ru.mcsnapix.snapiclans.settings.serializers.ClanRoleSerializer
import space.arim.dazzleconf.ConfigurationOptions

object Settings {
    private lateinit var databaseConfig: Configuration<DatabaseConfig>
    private lateinit var mainConfig: Configuration<MainConfig>
    private lateinit var messageConfig: Configuration<MessageConfig>
    val database get() = databaseConfig.data()
    val config get() = mainConfig.data()
    val message get() = messageConfig.data()

    fun enable() {
        val options = ConfigurationOptions.Builder()
            .addSerialiser(ClanRoleSerializer())
            .setCreateSingleElementCollections(true)
            .build()
        databaseConfig =
            Configuration.create("database.yml", DatabaseConfig::class.java, options)
        mainConfig =
            Configuration.create("config.yml", MainConfig::class.java, options)
        messageConfig =
            Configuration.create("message.yml", MessageConfig::class.java, options)
    }

    fun reload() {
        databaseConfig.reloadConfig()
        mainConfig.reloadConfig()
        messageConfig.reloadConfig()
    }
}