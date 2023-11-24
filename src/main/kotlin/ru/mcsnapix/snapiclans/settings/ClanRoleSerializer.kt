package ru.mcsnapix.snapiclans.settings

import ru.mcsnapix.snapiclans.api.roles.ClanPermission
import ru.mcsnapix.snapiclans.api.roles.ClanRole
import space.arim.dazzleconf.serialiser.Decomposer
import space.arim.dazzleconf.serialiser.FlexibleType
import space.arim.dazzleconf.serialiser.ValueSerialiser

class ClanRoleSerializer : ValueSerialiser<ClanRole> {
    companion object {
        private const val NAME = "name"
        private const val DISPLAY_NAME = "display_name"
        private const val WEIGHT = "weight"
        private const val PERMISSIONS = "permissions"
    }

    override fun getTargetClass() = ClanRole::class.java

    override fun deserialise(flexibleType: FlexibleType): ClanRole {
        val map = flexibleType.map.mapKeys { it.key.string }
        val s = map[PERMISSIONS]?.getList { obj: FlexibleType -> obj.string }
        val permissions: Set<ClanPermission> = if (s?.size == 1 && s[0] == "*") {
            ClanPermission.values().toSet()
        } else {
            map[PERMISSIONS]!!.getSet { flexType -> flexType.getObject(ClanPermission::class.java) }
        }

        return ClanRole(map[NAME]!!.string, map[DISPLAY_NAME]!!.string, map[WEIGHT]!!.integer, permissions)
    }

    override fun serialise(value: ClanRole, decomposer: Decomposer): Map<String, Any> {
        val map: MutableMap<String, Any> = LinkedHashMap(4)

        val permissions = if (value.permissions.containsAll(ClanPermission.values().toList())) {
            listOf("*")
        } else {
            decomposer.decomposeCollection(ClanPermission::class.java, value.permissions)
        }

        map[NAME] = value.name
        map[DISPLAY_NAME] = value.displayName
        map[WEIGHT] = value.weight
        map[PERMISSIONS] = permissions

        return map
    }
}