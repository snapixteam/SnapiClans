package ru.mcsnapix.snapiclans.settings

import org.bukkit.plugin.Plugin
import org.slf4j.Logger
import ru.mcsnapix.snapiclans.SnapiClans
import space.arim.dazzleconf.ConfigurationFactory
import space.arim.dazzleconf.ConfigurationOptions
import space.arim.dazzleconf.error.ConfigFormatSyntaxException
import space.arim.dazzleconf.error.InvalidConfigException
import space.arim.dazzleconf.ext.snakeyaml.CommentMode
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlConfigurationFactory
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlOptions
import space.arim.dazzleconf.helper.ConfigurationHelper
import java.io.IOException
import java.io.UncheckedIOException
import java.nio.file.Path

class Configuration<C> private constructor(private val logger: Logger, configHelper: ConfigurationHelper<C>) {
    private val configHelper: ConfigurationHelper<C>

    private var configData: C? = null

    init {
        this.configHelper = configHelper
        reloadConfig()
    }

    fun reloadConfig() {
        try {
            configData = configHelper.reloadConfigData()
        } catch (ex: IOException) {
            throw UncheckedIOException(ex)
        } catch (ex: ConfigFormatSyntaxException) {
            configData = configHelper.factory.loadDefaults()
            logger.error(
                "The yaml syntax in your configuration is invalid. "
                        + "Check your YAML syntax with a tool such as https://yaml-online-parser.appspot.com/", ex
            )
        } catch (ex: InvalidConfigException) {
            configData = configHelper.factory.loadDefaults()
            logger.error(
                "One of the values in your configuration is not valid. "
                        + "Check to make sure you have specified the right data types.", ex
            )
        }
    }

    fun data(): C {
        if (configData == null) {
            reloadConfig()
        }

        return configData!!
    }

    companion object {
        fun <C> create(
            fileName: String?,
            configClass: Class<C>?,
            options: ConfigurationOptions?
        ): Configuration<C> {
            val plugin = SnapiClans.instance
            return create(plugin.slF4JLogger, plugin.dataFolder.toPath(), fileName, configClass, options)
        }

        fun <C> create(
            plugin: Plugin,
            fileName: String?,
            configClass: Class<C>?,
            options: ConfigurationOptions?
        ): Configuration<C> {
            return create(plugin.slF4JLogger, plugin.dataFolder.toPath(), fileName, configClass, options)
        }

        fun <C> create(
            plugin: Plugin,
            configFolder: Path?,
            fileName: String?,
            configClass: Class<C>?,
            options: ConfigurationOptions?
        ): Configuration<C> {
            return create(plugin.slF4JLogger, configFolder, fileName, configClass, options)
        }

        private fun <C> create(
            logger: Logger,
            configFolder: Path?,
            fileName: String?,
            configClass: Class<C>?,
            options: ConfigurationOptions?
        ): Configuration<C> {
            val yamlOptions: SnakeYamlOptions = SnakeYamlOptions.Builder()
                .commentMode(CommentMode.alternativeWriter())
                .build()
            val configFactory: ConfigurationFactory<C> = SnakeYamlConfigurationFactory.create(
                configClass,
                options,
                yamlOptions
            )
            return Configuration<C>(
                logger,
                ConfigurationHelper(configFolder, fileName, configFactory)
            )
        }
    }
}
