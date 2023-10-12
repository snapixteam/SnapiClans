package ru.mcsnapix.snapiclansold.messenger.message

interface OutgoingMessage {
    fun asEncodedString(): String
}