package ru.mcsnapix.snapiclans.commands

import co.aikar.commands.BukkitCommandCompletionContext
import co.aikar.commands.PaperCommandManager
import com.google.common.collect.ImmutableList
import ru.mcsnapix.snapiclans.Part
import ru.mcsnapix.snapiclans.SnapiClans
import ru.mcsnapix.snapiclans.settings.Settings


object Commands : Part() {
    private val settings = Settings
    private val manager = PaperCommandManager(SnapiClans.instance)

    override fun enable() {
        registerCommandCompletions()
        registerCommandReplacements()
        manager.registerCommand(ClansCommand())
    }

    private fun registerCommandCompletions() {}

    private fun registerCommandReplacements() {
        val replacements = manager.commandReplacements
        val alias = settings.config.alias()
        replacements.addReplacement("clanscommand", alias.mainCommand())
        replacements.addReplacement("clanscommandcreate", alias.createCommand())
    }
}