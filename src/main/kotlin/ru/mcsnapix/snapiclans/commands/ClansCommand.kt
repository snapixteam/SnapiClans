package ru.mcsnapix.snapiclans.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import org.bukkit.entity.Player
import ru.mcsnapix.snapiclans.SnapiClans
import ru.mcsnapix.snapiclans.api.SnapiClansApi

@Suppress("unused")
@CommandAlias("%clanscommand")
class ClansCommand : BaseCommand() {
    @CommandPermission("snapiclans.admin")
    @Subcommand("admin")
    class ClanAdmin {
        @Subcommand("reload")
        fun reload() {
            SnapiClans.instance.reload()
        }
    }

    @Subcommand("%clanscommandcreate")
    fun create(player: Player, args: Array<String>) {
        // Если нет аргумента - пишем игроку: "используйте"
        // Если имя не валидное (Для имени клана - [A-z0-9]{3,16}, для отображение имени - [A-zА-я0-9&]{3,16}) - пишем игроку: "напишите корректно"
        // Если клан уже есть - пишем это
        // Если успешно создан - пишем это
        if (args.isEmpty()) {
            player.sendMessage("Нужен аргумент")
            return
        }

        val name = args[0]
        val displayName = if (args.size == 1) name else args[1]
        val owner = player.name

        SnapiClansApi.createClan(name, displayName, owner)
    }
}
