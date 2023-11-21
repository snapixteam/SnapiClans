package ru.mcsnapix.snapiclans.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.mcsnapix.snapiclans.Placeholder
import ru.mcsnapix.snapiclans.SnapiClans
import ru.mcsnapix.snapiclans.api.ClanAPI
import ru.mcsnapix.snapiclans.api.clans.ClanPermission
import ru.mcsnapix.snapiclans.caching.Messenger
import ru.mcsnapix.snapiclans.caching.actions.SendResultMessageAction
import ru.mcsnapix.snapiclans.caching.cache.ClanCaches
import ru.mcsnapix.snapiclans.caching.cache.UserCaches
import ru.mcsnapix.snapiclans.extensions.getLastLoginPlayer
import ru.mcsnapix.snapiclans.extensions.hasMoney
import ru.mcsnapix.snapiclans.extensions.send
import ru.mcsnapix.snapiclans.extensions.withdrawMoney
import ru.mcsnapix.snapiclans.managers.invite.InviteManager
import ru.mcsnapix.snapiclans.settings.Settings
import java.util.*

@Suppress("unused")
@CommandAlias("%clanscommand")
class ClanCommands : BaseCommand() {
    private val settings = Settings
    private val config = settings.config
    private val message = settings.message

    @CatchUnknown
    @Default
    @Subcommand("%clanscommandhelp")
    fun help(sender: CommandSender) {
        message.commands().help().forEach { sender.sendMessage(ChatColor.translateAlternateColorCodes('&', it)) }
    }

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
    fun createClan(player: Player, args: Array<String>) {
        val config = message.commands().createClan()
        val owner = player.name
        val regex = this.config.regex()

        if (UserCaches[owner] != null) {
            player.send(config.alreadyInClan())
            return
        }

        if (args.isEmpty() || args.size > 2) {
            player.send(config.use())
            return
        }

        val name = args[0]
        val displayName = if (args.size == 1) name else args[1]

        if (!regex.clanName().toRegex().matches(name)) {
            player.send(config.clanNameInvalid())
            return
        }
        if (!regex.clanDisplayName().toRegex().matches(displayName)) {
            player.send(config.clanDisplayNameInvalid())
            return
        }
        if (ClanCaches[name] != null) {
            player.send(config.clanAlreadyCreate())
            return
        }

        val price = this.config.economy().createClan()

        if (!player.hasMoney(price)) {
            player.send(config.noMoney())
            return
        }

        player.withdrawMoney(price)

        ClanAPI.createClan(name, displayName, owner)
        player.send(config.success())
    }

    @Subcommand("%clanscommandremove")
    fun removeClan(player: Player, args: Array<String>) {
        val config = message.commands().removeClan()
        val user = UserCaches[player.name]

        if (user == null) {
            player.send(config.noClan())
            return
        }

        if (!user.role.permissions.contains(ClanPermission.DISBAND)) {
            player.send(config.noPermission())
            return
        }

        if (args.size == 1 && args[0].lowercase() == "accept") {
            ClanAPI.removeClan(user.clan)
            player.send(config.success())
            return
        }

        player.send(config.accept())
    }

    @Subcommand("%clanscommandinvite")
    fun invite(player: Player, args: Array<String>) {
        val config = message.commands().invite()
        val name = player.name
        val user = UserCaches[name]

        if (user == null) {
            player.send(config.noClan())
            return
        }

        if (!user.role.permissions.contains(ClanPermission.INVITE)) {
            player.send(config.noPermission())
            return
        }

        if (args.isEmpty()) {
            player.send(config.use())
            return
        }

        var receiver = args[0]
        if (name == receiver) {
            player.send(config.cannotSelf())
            return
        }
        val lastLoginReceiver = getLastLoginPlayer(receiver)
        if (lastLoginReceiver == null) {
            player.send(config.notExist(), Placeholder("name", receiver))
            return
        }
        receiver = lastLoginReceiver.name
        if (!lastLoginReceiver.isOnline) {
            player.send(config.offline(), Placeholder("name", receiver))
            return
        }

        val userReceiver = UserCaches[receiver]
        if (userReceiver != null) {
            player.send(config.alreadyClan(), Placeholder("name", receiver))
            return
        }

        if (InviteManager.get(name, receiver) != null) {
            player.send(config.alreadyInvite(), Placeholder("name", receiver))
            return
        }

        InviteManager.add(user.clan, name, receiver)
        player.send(config.success())
        Messenger.sendOutgoingMessage(
            SendResultMessageAction(
                UUID.randomUUID(),
                receiver,
                config.acceptOrDecline(),
                Placeholder("sender", name),
                Placeholder("clan", user.clan.name)
            )
        )
    }

    @Subcommand("%clanscommandaccept")
    fun accept(player: Player, args: Array<String>) {
        val config = message.commands().accept()
        val receiver = player.name
        val userReceiver = UserCaches[receiver]

        if (userReceiver != null) {
            player.send(config.alreadyClan())
            return
        }

        if (args.isEmpty()) {
            player.send(config.use())
            return
        }
        var sender = args[0]

        if (InviteManager.get(name, sender) == null) {
            player.send(config.notInvite(), Placeholder("name", sender))
            return
        }
        sender = getLastLoginPlayer(sender)?.name ?: sender

        InviteManager.accept(sender, player)
    }

    @Subcommand("%clanscommanddecline")
    fun decline(player: Player, args: Array<String>) {
        val config = message.commands().decline()

        if (args.isEmpty()) {
            player.send(config.use())
            return
        }
        var sender = args[0]

        if (InviteManager.get(name, sender) == null) {
            player.send(config.notInvite(), Placeholder("name", sender))
            return
        }
        sender = getLastLoginPlayer(sender)?.name ?: sender

        InviteManager.decline(sender, player)
    }

    @Subcommand("%clanscommandchat")
    fun sendMessage(player: Player, args: Array<String>) {
        val config = message.commands().chat()
        val user = UserCaches[player.name]

        if (user == null) {
            player.send(config.noClan())
            return
        }

        if (args.isEmpty()) {
            player.send(config.writeMessage())
            return
        }

        val message = StringBuilder()
        for (arg in args) {
            message.append(arg).append(" ")
        }

        ClanAPI.sendMessage(player, user.clan, message.toString())
    }

    @Subcommand("%clanscommandrole")
    fun role(player: Player, args: Array<String>) {
        val config = message.commands().role()
        val user = UserCaches[player.name]

        if (user == null) {
            player.send(config.noClan())
            return
        }

        if (!user.role.permissions.contains(ClanPermission.SET_ROLE)) {
            player.send(config.noPermission())
            return
        }

        // increase/decrease <name>
//        if (args.isEmpty()) {
//            player.send(config.)
//            return
//        }

    }
}