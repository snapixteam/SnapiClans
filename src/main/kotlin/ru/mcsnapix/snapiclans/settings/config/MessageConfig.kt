package ru.mcsnapix.snapiclans.settings.config

import space.arim.dazzleconf.annote.ConfDefault.DefaultString
import space.arim.dazzleconf.annote.ConfDefault.DefaultStrings
import space.arim.dazzleconf.annote.ConfKey
import space.arim.dazzleconf.annote.SubSection

interface MessageConfig {
    @SubSection
    fun commands(): Commands
    interface Commands {
        @DefaultStrings("Hello", "world??", "!")
        fun help(): List<String>

        @SubSection
        fun create(): CreateCommand
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

        @SubSection
        fun remove(): RemoveCommand
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

            @ConfKey("accept-or-decline")
            @DefaultString("&aНажмите, чтобы потвердить удаление (clans accept player, clans decline player)")
            fun acceptOrDecline(): String

            @ConfKey("success")
            @DefaultString("&aВы отправили приглашение")
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
    }
}