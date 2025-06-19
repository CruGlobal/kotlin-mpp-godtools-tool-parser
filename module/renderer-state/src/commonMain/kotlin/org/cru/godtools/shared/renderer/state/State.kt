package org.cru.godtools.shared.renderer.state

import androidx.annotation.RestrictTo
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.native.HiddenFromObjC
import org.cru.godtools.shared.renderer.state.internal.Parcelable
import org.cru.godtools.shared.renderer.state.internal.Parcelize
import org.cru.godtools.shared.tool.parser.expressions.ExpressionContext
import org.cru.godtools.shared.tool.parser.expressions.SimpleExpressionContext

@JsExport
@Parcelize
@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
class State internal constructor(
    private val triggeredAnalyticsEvents: MutableMap<String, Int> = mutableMapOf(),
    private val vars: MutableMap<String, List<String>?> = mutableMapOf(),
) : Parcelable, ExpressionContext by SimpleExpressionContext(vars) {
    @JsName("createState")
    constructor() : this(vars = mutableMapOf())

    // region Analytics Events Tracking
    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun getTriggeredAnalyticsEventsCount(id: String) = triggeredAnalyticsEvents[id] ?: 0
    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun recordTriggeredAnalyticsEvent(id: String) {
        triggeredAnalyticsEvents[id] = (triggeredAnalyticsEvents[id] ?: 0) + 1
    }
    // endregion Analytics Events Tracking
}
