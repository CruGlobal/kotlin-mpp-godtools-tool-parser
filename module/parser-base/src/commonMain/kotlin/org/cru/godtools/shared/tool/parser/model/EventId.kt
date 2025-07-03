package org.cru.godtools.shared.tool.parser.model

import androidx.annotation.RestrictTo
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.native.HiddenFromObjC

@JsExport
@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
class EventId(val namespace: String? = null, val name: String) {
    companion object {
        private const val NAMESPACE_FOLLOWUP = "followup"
        @HiddenFromObjC
        @JsExport.Ignore
        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        const val NAMESPACE_STATE = "state"

        val FOLLOWUP = EventId(NAMESPACE_FOLLOWUP, "send")
    }

    override fun equals(other: Any?) = other is EventId &&
        namespace == other.namespace &&
        name.equals(other.name, true)

    override fun hashCode() = (namespace?.hashCode() ?: 0) * 31 + name.lowercase().hashCode()
    override fun toString() = if (namespace != null) "$namespace:$name" else name
}
