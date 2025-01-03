package org.cru.godtools.shared.tool.parser.model

import androidx.annotation.VisibleForTesting
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.native.HiddenFromObjC
import org.cru.godtools.shared.tool.parser.util.REGEX_SEQUENCE_SEPARATOR
import org.cru.godtools.shared.tool.state.State

private const val EVENT_NAMESPACE_FOLLOWUP = "followup"
@VisibleForTesting
internal const val EVENT_NAMESPACE_STATE = "state"

@JsExport
@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
class EventId internal constructor(val namespace: String? = null, val name: String) {
    companion object {
        val FOLLOWUP = EventId(EVENT_NAMESPACE_FOLLOWUP, "send")

        @HiddenFromObjC
        @JsExport.Ignore
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

    fun resolve(state: State) = when (namespace) {
        EVENT_NAMESPACE_STATE -> state.getVar(name).map { EventId(name = it) }
        else -> listOf(this)
    }

    override fun equals(other: Any?) = other is EventId &&
        namespace == other.namespace &&
        name.equals(other.name, true)

    override fun hashCode() = (namespace?.hashCode() ?: 0) * 31 + name.lowercase().hashCode()
    override fun toString() = if (namespace != null) "$namespace:$name" else name
}

internal inline fun String?.toEventIds() = EventId.parse(this)
