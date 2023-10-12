package ru.mcsnapix.snapiclans.registry.invite

import ru.mcsnapix.snapiclans.api.clans.Clan

data class Invite(val clan: Clan, val timestamp: Long)