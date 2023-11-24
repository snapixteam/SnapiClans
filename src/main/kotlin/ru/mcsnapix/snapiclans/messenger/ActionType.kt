package ru.mcsnapix.snapiclans.messenger

import com.google.gson.JsonElement
import ru.mcsnapix.snapiclans.messenger.actions.*
import java.util.*

enum class ActionType {
    CREATE_CLAN {
        override fun decode(content: JsonElement, id: UUID): Action {
            return CreateClanAction.decode(content, id)
        }
    },
    REMOVE_CLAN {
        override fun decode(content: JsonElement, id: UUID): Action {
            return RemoveClanAction.decode(content, id)
        }
    },
    UPDATE_CLAN {
        override fun decode(content: JsonElement, id: UUID): Action {
            return UpdateClanAction.decode(content, id)
        }
    },
    CREATE_USER {
        override fun decode(content: JsonElement, id: UUID): Action {
            return CreateUserAction.decode(content, id)
        }
    },
    REMOVE_USER {
        override fun decode(content: JsonElement, id: UUID): Action {
            return RemoveUserAction.decode(content, id)
        }
    },
    UPDATE_USER {
        override fun decode(content: JsonElement, id: UUID): Action {
            return UpdateUserAction.decode(content, id)
        }
    },
    SEND_CHAT_MESSAGE {
        override fun decode(content: JsonElement, id: UUID): Action {
            return SendChatMessageAction.decode(content, id)
        }
    },
    SEND_MESSAGE {
        override fun decode(content: JsonElement, id: UUID): Action {
            return SendMessageAction.decode(content, id)
        }
    },
    RESPONSE_INVITE {
        override fun decode(content: JsonElement, id: UUID): Action {
            return ResponseInviteAction.decode(content, id)
        }
    };

    abstract fun decode(content: JsonElement, id: UUID): Action
}