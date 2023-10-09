package ru.mcsnapix.snapiclans.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Subcommand
import ru.mcsnapix.snapiclans.SnapiClans

@Suppress("unused")
@CommandAlias("%clanscommand")
class ClansCommand : BaseCommand() {
    @Subcommand("admin")
    class ClanAdmin : BaseCommand() {
        @Subcommand("reload")
        fun reload() {
            SnapiClans.reload()
        }
    }
}
