package ru.mcsnapix.snapiclans.messenger.message

interface OutgoingMessage {
    fun asEncodedString(): String
}