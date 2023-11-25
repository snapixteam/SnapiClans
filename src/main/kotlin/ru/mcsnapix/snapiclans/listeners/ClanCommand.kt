package ru.mcsnapix.snapiclans.listeners

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.mcsnapix.snapiclans.*
import ru.mcsnapix.snapiclans.api.roles.ClanPermission
import ru.mcsnapix.snapiclans.api.roles.ClanRole
import ru.mcsnapix.snapiclans.database.*
import ru.mcsnapix.snapiclans.settings.Settings

@Suppress("unused")
@CommandAlias("%clanscommand")
class ClanCommand : BaseCommand() {
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

        if (player.hasClan()) {
            player.send(config.alreadyInClan())
            return
        }

        if (args.isEmpty() || args.size > 2) {
            player.send(config.use())
            return
        }

        val clanName = args[0]
        val clanDisplayName = if (args.size == 1) clanName else args[1]

        if (!this.config.regex().clanName().toRegex().matches(clanName)) {
            player.send(config.clanNameInvalid())
            return
        }
        if (!this.config.regex().clanDisplayName().toRegex().matches(clanDisplayName)) {
            player.send(config.clanDisplayNameInvalid())
            return
        }

        if (ClanCache.get { it.name == clanName } != null) {
            player.send(config.clanAlreadyCreate())
            return
        }

        val price = this.config.economy().createClan()
        if (!player.hasMoney(price)) {
            player.send(config.noMoney())
            return
        }

        player.withdrawMoney(price)

        runBlocking {
            val clan = async {
                ClanService.create(ExposedClan(clanName, clanDisplayName, player.name))
            }.await()
            val user = async {
                UserService.create(ExposedUser(clan.id, player.name, ClanRole.owner()))
            }.await()
            player.send(config.success(), Placeholder("name", user.name), Placeholder("clan", clan.name))
        }
    }

    @Subcommand("%clanscommandremove")
    fun removeClan(player: Player, args: Array<String>) {
        val config = message.commands().removeClan()

        val user = player.toUser()
        val clan = ClanCache.get { it.id == user?.clanId }
        if (user == null || clan == null) {
            player.send(config.noClan())
            return
        }

        if (!user.hasPermission(ClanPermission.DISBAND)) {
            player.send(config.noPermission())
            return
        }

        if (args.size == 1 && args[0].lowercase() == "accept") {
            runBlocking {
                async {
                    clan.members().forEach {
                        UserService.delete(it.name)
                    }
                }.await()
                async {
                    ClanService.delete(clan.name)
                }.await()
                player.send(config.success())
            }
            return
        }

        player.send(config.accept())
    }
}