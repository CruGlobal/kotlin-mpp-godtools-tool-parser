package org.cru.godtools.tool.model

import org.cru.godtools.tool.REGEX_SEQUENCE_SEPARATOR

class EventId internal constructor(val namespace: String? = null, val name: String) {
    override fun equals(other: Any?) = other is EventId &&
        namespace == other.namespace &&
        name.equals(other.name, true)

    override fun hashCode() = (namespace?.hashCode() ?: 0) * 31 + name.lowercase().hashCode()
    override fun toString() = if (namespace != null) "$namespace:$name" else name

    companion object {
        val FOLLOWUP = EventId(namespace = "followup", name = "send")

        fun parse(raw: String?) = raw
            ?.split(REGEX_SEQUENCE_SEPARATOR)
            ?.mapNotNull {
                val components = it.split(':', limit = 2)
                when {
                    it.isEmpty() -> null
                    components.size == 1 -> EventId(name = it)
                    else -> EventId(components[0], components[1])
                }
            }.orEmpty()
    }
}
