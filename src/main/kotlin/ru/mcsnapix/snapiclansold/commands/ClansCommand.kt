package ru.mcsnapix.snapiclansold.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.mcsnapix.snapiclansold.SnapiClans
import ru.mcsnapix.snapiclansold.api.SnapiClansApi
import ru.mcsnapix.snapiclansold.api.clans.ClanPermission
import ru.mcsnapix.snapiclans.extensions.clanUser
import ru.mcsnapix.snapiclans.extensions.hasMoney
import ru.mcsnapix.snapiclans.extensions.send
import ru.mcsnapix.snapiclans.extensions.withdrawMoney
import ru.mcsnapix.snapiclansold.messenger.SQLMessenger
import ru.mcsnapix.snapiclansold.messenger.message.MessageChatMessage
import ru.mcsnapix.snapiclans.settings.Settings
import java.util.*

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

    @CatchUnknown
    @Default
    @Subcommand("%clanscommandhelp")
    fun help(sender: CommandSender) {
        message.commands().help().forEach { sender.sendMessage(ChatColor.translateAlternateColorCodes('&', it)) }
    }

    @Subcommand("%clanscommandcreate")
    fun create(player: Player, args: Array<String>) {
        val owner = player.name
        val create = message.commands().create()
        val regex = config.regex()

        if (SnapiClansApi.user(owner) != null) {
            ru.mcsnapix.snapiclans.extensions.send(create.alreadyInClan())
            return
        }

        if (args.isEmpty() || args.size > 2) {
            ru.mcsnapix.snapiclans.extensions.send(create.use())
            return
        }

        val name = args[0]
        val displayName = if (args.size == 1) name else args[1]

        if (!regex.clanName().toRegex().matches(name)) {
            ru.mcsnapix.snapiclans.extensions.send(create.clanNameInvalid())
            return
        }
        if (!regex.clanDisplayName().toRegex().matches(displayName)) {
            ru.mcsnapix.snapiclans.extensions.send(create.clanDisplayNameInvalid())
            return
        }
        if (SnapiClansApi.clan(name) != null) {
            ru.mcsnapix.snapiclans.extensions.send(create.clanAlreadyCreate())
            return
        }

        val price = config.economy().createClan()

        if (!ru.mcsnapix.snapiclans.extensions.hasMoney(price)) {
            ru.mcsnapix.snapiclans.extensions.send(create.noMoney())
            return
        }

        ru.mcsnapix.snapiclans.extensions.withdrawMoney(price)
        ru.mcsnapix.snapiclans.extensions.send(create.success())

        with(SnapiClansApi) {
            createClan(name, displayName, owner)
            clan(name)?.let { createUser(player.name, it, RolesRegistry.owner) }
        }
    }

    @Subcommand("%clanscommandremove")
    fun remove(player: Player, args: Array<String>) {
        val remove = message.commands().remove()
        val clanUser = ru.mcsnapix.snapiclans.extensions.clanUser

        if (clanUser == null) {
            ru.mcsnapix.snapiclans.extensions.send(remove.noClan())
            return
        }

        if (!clanUser.role.permissions.contains(ClanPermission.DISBAND)) {
            ru.mcsnapix.snapiclans.extensions.send(remove.noPermission())
            return
        }

        if (args.size == 1) {
            if (args[0].lowercase() == "accept") {
                SnapiClansApi.removeClan(clanUser.clan.name)
                ru.mcsnapix.snapiclans.extensions.send(remove.success())
                return
            }
        }

        ru.mcsnapix.snapiclans.extensions.send(remove.accept())
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
        val chat = message.commands().chat()
        val user = SnapiClansApi.user(player.name)

        if (user == null) {
            ru.mcsnapix.snapiclans.extensions.send(chat.noClan())
            return
        }

        if (args.isEmpty()) {
            ru.mcsnapix.snapiclans.extensions.send(chat.writeMessage())
            return
        }

        val message = StringBuilder()
        for (arg in args) {
            message.append(arg).append(" ")
        }
        var msg = message.toString()

        if (!player.hasPermission("snapiclans.chat.color")) {
            msg = "&([A-z0-9])".toRegex().replace(msg, "")
        }

        ru.mcsnapix.snapiclans.extensions.send(
            Settings.config.chatFormat().replace("%message%", msg)
                .replace("%sender%", player.name)
        )
        SQLMessenger.sendOutgoingMessage(MessageChatMessage(UUID.randomUUID(), user.name, user.clan.name, msg))
    }
}
