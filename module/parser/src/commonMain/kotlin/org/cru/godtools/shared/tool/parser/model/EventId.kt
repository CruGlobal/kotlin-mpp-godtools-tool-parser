package org.cru.godtools.shared.tool.parser.model

import androidx.annotation.VisibleForTesting
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import org.cru.godtools.shared.tool.parser.expressions.ExpressionContext
import org.cru.godtools.shared.tool.parser.util.REGEX_SEQUENCE_SEPARATOR

@JsExport
@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
class EventId(val namespace: String? = null, val name: String) {
    companion object {
        private const val NAMESPACE_FOLLOWUP = "followup"
        @VisibleForTesting
        internal const val NAMESPACE_STATE = "state"

        val FOLLOWUP = EventId(NAMESPACE_FOLLOWUP, "send")

        internal fun String.toEventIds() = split(REGEX_SEQUENCE_SEPARATOR)
            .mapNotNull {
                val components = it.split(':', limit = 2)
                when {
                    it.isBlank() -> null
                    components.size == 1 -> EventId(name = it)
                    else -> EventId(components[0], components[1])
                }
            }
    }

    fun resolve(ctx: ExpressionContext): Array<EventId> = when (namespace) {
        NAMESPACE_STATE -> ctx.getVar(name).map { EventId(name = it) }.toTypedArray()
        else -> arrayOf(this)
    }

    override fun equals(other: Any?) = other is EventId &&
        namespace == other.namespace &&
        name.equals(other.name, true)

    override fun hashCode() = (namespace?.hashCode() ?: 0) * 31 + name.lowercase().hashCode()
    override fun toString() = if (namespace != null) "$namespace:$name" else name
}
