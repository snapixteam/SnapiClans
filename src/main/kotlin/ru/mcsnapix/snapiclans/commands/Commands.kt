package ru.mcsnapix.snapiclans.commands

import co.aikar.commands.PaperCommandManager
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
        replacements.addReplacement("clanscommandhelp", alias.helpCommand())
        replacements.addReplacement("clanscommandcreate", alias.createCommand())
        replacements.addReplacement("clanscommandremove", alias.removeCommand())
        replacements.addReplacement("clanscommandinvite", alias.inviteCommand())
        replacements.addReplacement("clanscommandaccept", alias.acceptCommand())
        replacements.addReplacement("clanscommanddecline", alias.declineCommand())
        replacements.addReplacement("clanscommandrole", alias.roleCommand())
        replacements.addReplacement("clanscommandleave", alias.leaveCommand())
        replacements.addReplacement("clanscommandchat", alias.chatCommand())
    }
}