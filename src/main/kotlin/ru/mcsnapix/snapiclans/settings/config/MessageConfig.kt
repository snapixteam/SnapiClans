package ru.mcsnapix.snapiclans.settings.config

import space.arim.dazzleconf.annote.ConfDefault.DefaultString
import space.arim.dazzleconf.annote.ConfDefault.DefaultStrings
import space.arim.dazzleconf.annote.ConfKey
import space.arim.dazzleconf.annote.SubSection

interface MessageConfig {
    @ConfKey("response-invite")
    @SubSection
    fun responseInvite(): ResponseInvite
    interface ResponseInvite {
        @ConfKey("accept")
        @DefaultString("Игрок %receiver% принял ваше приглашение в клан %clan%")
        fun accept(): String

        @ConfKey("decline")
        @DefaultString("Игрок %receiver% отказался от вашего приглашения в клан %clan%")
        fun decline(): String

        @ConfKey("ignore")
        @DefaultString("Игрок %receiver% проигнорировал ваше приглашение в клан %clan%")
        fun ignore(): String
    }

    @SubSection
    fun commands(): Commands
    interface Commands {
        @DefaultStrings("Hello", "world??", "!")
        fun help(): List<String>

        @ConfKey("create-clan")
        @SubSection
        fun createClan(): CreateCommand
        interface CreateCommand {
            @ConfKey("already-in-clan")
            @DefaultString("&cВы уже в клане")
            fun alreadyInClan(): String

            @ConfKey("use")
            @DefaultString("&cИспользуйте: &a/clans create <название> [отображаемое название]")
            fun use(): String

            @ConfKey("clan-name-invalid")
            @DefaultString("&cВы не правильно написали название клана")
            fun clanNameInvalid(): String

            @ConfKey("clan-displayname-invalid")
            @DefaultString("&cВы не правильно написали отображаемое название клана")
            fun clanDisplayNameInvalid(): String

            @ConfKey("clan-already-create")
            @DefaultString("&cКлан уже создан")
            fun clanAlreadyCreate(): String

            @ConfKey("no-money")
            @DefaultString("&cУ вас денег нет")
            fun noMoney(): String

            @ConfKey("success")
            @DefaultString("&aВы успешно создали клан")
            fun success(): String
        }

        @ConfKey("remove-clan")
        @SubSection
        fun removeClan(): RemoveCommand
        interface RemoveCommand {
            @ConfKey("no-clan")
            @DefaultString("&cВы не в клане")
            fun noClan(): String

            @ConfKey("no-permission")
            @DefaultString("&cУ вас нет разрешения, чтобы сделать это")
            fun noPermission(): String

            @ConfKey("accept")
            @DefaultString("&aНажмите, чтобы потвердить удаление")
            fun accept(): String

            @ConfKey("success")
            @DefaultString("&aВы успешно удалили клан")
            fun success(): String
        }

        @SubSection
        fun invite(): InviteCommand
        interface InviteCommand {
            @ConfKey("no-clan")
            @DefaultString("&cВы не в клане")
            fun noClan(): String

            @ConfKey("no-permission")
            @DefaultString("&cУ вас нет разрешения, чтобы отправлять приглашение")
            fun noPermission(): String

            @ConfKey("use")
            @DefaultString("&fИспользуйте: /clans invite player")
            fun use(): String

            @ConfKey("already-in-clan")
            @DefaultString("&cИгрок уже в клане")
            fun alreadyClan(): String

            @ConfKey("already-invite")
            @DefaultString("&cВы уже пригласили игрока")
            fun alreadyInvite(): String

            @ConfKey("accept-or-decline")
            @DefaultString("&aНажмите, чтобы ответить на приглашение (clans accept player, clans decline player)")
            fun acceptOrDecline(): String

            @ConfKey("success")
            @DefaultString("&aВы отправили приглашение")
            fun success(): String
        }

        @SubSection
        fun accept(): AcceptCommand
        interface AcceptCommand {
            @ConfKey("already-in-clan")
            @DefaultString("&cВы уже в клане")
            fun alreadyClan(): String

            @ConfKey("use")
            @DefaultString("&fИспользуйте: /clans accept player")
            fun use(): String

            @ConfKey("error-sender-not-in-clan")
            @DefaultString("&aИгрок, который вас пригласил, вышел из клана, поэтому вы не можете принять его приглашение")
            fun error(): String

            @ConfKey("success")
            @DefaultString("&aВы вступили в клан %name%")
            fun success(): String
        }

        @SubSection
        fun decline(): DeclineCommand
        interface DeclineCommand {
            @ConfKey("use")
            @DefaultString("&fИспользуйте: /clans accept player")
            fun use(): String
            
            @ConfKey("error-sender-not-in-clan")
            @DefaultString("&aИгрок, который вас пригласил, вышел из клана, поэтому вы не можете отказаться его приглашение")
            fun error(): String

            @ConfKey("success")
            @DefaultString("&aВы вступили в клан %name%")
            fun success(): String
        }

        @SubSection
        fun chat(): ChatCommand
        interface ChatCommand {
            @ConfKey("no-clan")
            @DefaultString("&cВы не в клане")
            fun noClan(): String

            @ConfKey("write-message")
            @DefaultString("&aНапишите сообщение")
            fun writeMessage(): String
        }

        @SubSection
        fun role(): RoleCommand
        interface RoleCommand {
            @ConfKey("no-clan")
            @DefaultString("&cВы не в клане")
            fun noClan(): String

            @ConfKey("no-permission")
            @DefaultString("&cУ вас нет разрешения, чтобы изменить роль")
            fun noPermission(): String

            @ConfKey("use")
            @DefaultString("&cИспользуйте: /clan role increase/decrease <имя>")
            fun use(): String

            @ConfKey("success-increase")
            @DefaultString("&cВы успешно повысили %player_name%")
            fun successIncrease(): String

            @ConfKey("success-decrease")
            @DefaultString("&cВы успешно понизили %player_name%")
            fun successDecrease(): String
        }
    }
}