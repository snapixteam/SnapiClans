package ru.mcsnapix.snapiclans.commands

import co.aikar.commands.PaperCommandManager
import ru.mcsnapix.snapiclans.Part
import ru.mcsnapix.snapiclans.SnapiClans
import ru.mcsnapix.snapiclans.settings.Settings

object Commands : Part() {
    private val settings = Settings
    private val manager = PaperCommandManager(SnapiClans)

    override fun enable() {
        manager.registerCommand(ClansCommand())
        registerCommandCompletions()
        registerCommandReplacements()
    }

    private fun registerCommandCompletions() {}

    private fun registerCommandReplacements() {
        val replacements = manager.commandReplacements
        val alias = settings.config.alias()
        replacements.addReplacement("clanscommand", alias.mainCommand())
        replacements.addReplacement("clanscommandcreate", alias.createCommand())
    }
}