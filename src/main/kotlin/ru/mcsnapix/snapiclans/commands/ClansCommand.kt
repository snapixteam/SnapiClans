package ru.mcsnapix.snapiclans.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.mcsnapix.snapiclans.SnapiClans
import ru.mcsnapix.snapiclans.api.SnapiClansApi
import ru.mcsnapix.snapiclans.api.clans.ClanPermission
import ru.mcsnapix.snapiclans.extensions.clanUser
import ru.mcsnapix.snapiclans.extensions.hasMoney
import ru.mcsnapix.snapiclans.extensions.send
import ru.mcsnapix.snapiclans.extensions.withdrawMoney
import ru.mcsnapix.snapiclans.registry.ClansRegistry
import ru.mcsnapix.snapiclans.registry.RolesRegistry
import ru.mcsnapix.snapiclans.settings.Settings

@Suppress("unused")
@CommandAlias("%clanscommand")
class ClansCommand : BaseCommand() {
    private val settings = Settings
    private val config = settings.config
    private val message = settings.message

    @CommandPermission("snapiclans.admin")
    @Subcommand("admin")
    fun admin(sender: CommandSender, args: Array<String>) {
        if (args.size == 1) {
            when (args[0].lowercase()) {
                "reload" -> {
                    sender.sendMessage("§aSuccess reload plugin")
                    SnapiClans.instance.reload()
                }
            }
        }
    }

    @Subcommand("%clanscommandcreate")
    fun create(player: Player, args: Array<String>) {
        val owner = player.name
        val create = message.commands().create()
        val regex = config.regex()

        if (SnapiClansApi.user(owner) != null) {
            player.send(create.alreadyInClan())
            return
        }

        if (args.isEmpty()) {
            player.send(create.use())
            return
        }

        val name = args[0]
        val displayName = if (args.size == 1) name else args[1]

        if (!regex.clanName().toRegex().matches(name)) {
            player.send(create.clanNameInvalid())
            return
        }
        if (!regex.clanDisplayName().toRegex().matches(displayName)) {
            player.send(create.clanDisplayNameInvalid())
            return
        }
        if (ClansRegistry.get(name) != null) {
            player.send(create.clanAlreadyCreate())
            return
        }

        val price = config.economy().createClan()

        if (!player.hasMoney(price)) {
            player.send(create.noMoney())
            return
        }

        player.withdrawMoney(price)
        player.send(create.success())

        with(SnapiClansApi) {
            createClan(name, displayName, owner)
            clan(name)?.let { createUser(player.name, it, RolesRegistry.owner) }
        }
    }

    @Subcommand("%clanscommandremove")
    fun remove(player: Player, args: Array<String>) {
        val remove = message.commands().remove()
        val clanUser = player.clanUser

        if (clanUser == null) {
            player.send(remove.noClan())
            return
        }

        if (!clanUser.role.permissions.contains(ClanPermission.DISBAND)) {
            player.send(remove.noPermission())
            return
        }

        if (args.size == 1) {
            if (args[0].lowercase() == "accept") {
                SnapiClansApi.removeClan(clanUser.clan.name)
                player.send(remove.success())
                return
            }
        }

        player.send(remove.accept())
    }

    @Subcommand("%clanscommandinvite")
    fun invite(player: Player, args: Array<String>) {

    }

    @Subcommand("%clanscommandaccept")
    fun accept(player: Player, args: Array<String>) {

    }

    @Subcommand("%clanscommanddecline")
    fun decline(player: Player, args: Array<String>) {

    }

    @Subcommand("%clanscommandrole")
    fun role(player: Player, args: Array<String>) {

    }

    @Subcommand("%clanscommandleave")
    fun leave(player: Player, args: Array<String>) {

    }

    @Subcommand("%clanscommandchat")
    fun chat(player: Player, args: Array<String>) {

    }
}
