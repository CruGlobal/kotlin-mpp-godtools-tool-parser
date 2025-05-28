package org.cru.godtools.shared.tool.parser.model

import androidx.annotation.RestrictTo
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.native.HiddenFromObjC

@JsExport
@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
data class AnalyticsEvent(
    val action: String = "",
    val attributes: Map<String, String> = emptyMap(),
    private val trigger: Trigger = Trigger.DEFAULT,
    val delay: Int = 0,
    @JsExport.Ignore
    val systems: Set<System> = emptySet(),
    @HiddenFromObjC
    @JsExport.Ignore
    @get:RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val id: String = action,
    @HiddenFromObjC
    @JsExport.Ignore
    @get:RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    val limit: Int? = null,
) {
    enum class System { FACEBOOK, FIREBASE, USER }
    enum class Trigger { VISIBLE, HIDDEN, CLICKED, DEFAULT, UNKNOWN }

    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    companion object;

    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun isTriggerType(vararg types: Trigger) = types.contains(trigger)
    fun isForSystem(system: System) = systems.contains(system)
}
