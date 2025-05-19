package org.cru.godtools.shared.tool.parser.model

import androidx.annotation.VisibleForTesting
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import org.cru.godtools.shared.tool.state.State

private const val EVENT_NAMESPACE_FOLLOWUP = "followup"
@VisibleForTesting
internal const val EVENT_NAMESPACE_STATE = "state"

@JsExport
@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
class EventId(val namespace: String? = null, val name: String) {
    companion object {
        val FOLLOWUP = EventId(EVENT_NAMESPACE_FOLLOWUP, "send")
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
