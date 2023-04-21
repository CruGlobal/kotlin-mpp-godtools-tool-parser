package org.cru.godtools.shared.tool.parser.model

import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.native.HiddenFromObjC

@JsExport
@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
interface HasAnalyticsEvents {
    @JsName("_getAnalyticsEvents")
    fun getAnalyticsEvents(type: AnalyticsEvent.Trigger): List<AnalyticsEvent>

    // region Kotlin/JS interop
    @HiddenFromObjC
    @JsName("getAnalyticsEvents")
    fun jsGetAnalyticsEvents(type: AnalyticsEvent.Trigger) = getAnalyticsEvents(type).toTypedArray()
    // endregion Kotlin/JS interop
}
