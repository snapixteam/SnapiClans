package ru.mcsnapix.snapiclans.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.mcsnapix.snapiclans.SnapiClans
import ru.mcsnapix.snapiclans.api.ClanAPI
import ru.mcsnapix.snapiclans.api.clans.ClanPermission
import ru.mcsnapix.snapiclans.caching.cache.ClanCaches
import ru.mcsnapix.snapiclans.caching.cache.UserCaches
import ru.mcsnapix.snapiclans.extensions.hasMoney
import ru.mcsnapix.snapiclans.extensions.send
import ru.mcsnapix.snapiclans.extensions.withdrawMoney
import ru.mcsnapix.snapiclans.settings.Settings
import java.util.*

@Suppress("unused")
@CommandAlias("%clanscommand")
class ClanCommands : BaseCommand() {
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

    @CatchUnknown
    @Default
    @Subcommand("%clanscommandhelp")
    fun help(sender: CommandSender) {
        message.commands().help().forEach { sender.sendMessage(ChatColor.translateAlternateColorCodes('&', it)) }
    }

    @Subcommand("%clanscommandcreate")
    fun createClan(player: Player, args: Array<String>) {
        val owner = player.name
        val create = message.commands().createClan()
        val regex = config.regex()

        if (UserCaches[owner] != null) {
            player.send(create.alreadyInClan())
            return
        }

        if (args.isEmpty() || args.size > 2) {
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
        if (ClanCaches[name] != null) {
            player.send(create.clanAlreadyCreate())
            return
        }

        val price = config.economy().createClan()

        if (!player.hasMoney(price)) {
            player.send(create.noMoney())
            return
        }

        player.withdrawMoney(price)

        ClanAPI.createClan(name, displayName, owner)
        player.send(create.success())
    }

    @Subcommand("%clanscommandremove")
    fun removeClan(player: Player, args: Array<String>) {
        val remove = message.commands().removeClan()
        val user = UserCaches[player.name]

        if (user == null) {
            player.send(remove.noClan())
            return
        }

        if (!user.role.permissions.contains(ClanPermission.DISBAND)) {
            player.send(remove.noPermission())
            return
        }

        if (args.size == 1) {
            if (args[0].lowercase() == "accept") {
                ClanAPI.removeClan(user.clan)
                player.send(remove.success())
                return
            }
        }

        player.send(remove.accept())
    }

    @Subcommand("%clanscommandchat")
    fun sendMessage(player: Player, args: Array<String>) {
        val chat = message.commands().chat()
        val user = UserCaches[player.name]

        if (user == null) {
            player.send(chat.noClan())
            return
        }

        if (args.isEmpty()) {
            player.send(chat.writeMessage())
            return
        }

        val message = StringBuilder()
        for (arg in args) {
            message.append(arg).append(" ")
        }

        ClanAPI.sendMessage(player, user.clan, message.toString())
    }
}