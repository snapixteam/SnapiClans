package ru.mcsnapix.snapiclansold.registry.invite

import ru.mcsnapix.snapiclansold.api.clans.Clan

data class Invite(val clan: Clan, val timestamp: Long)