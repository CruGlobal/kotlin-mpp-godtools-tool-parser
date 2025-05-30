package org.cru.godtools.shared.renderer.state

import androidx.annotation.RestrictTo
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.native.HiddenFromObjC
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onSubscription
import org.cru.godtools.shared.renderer.state.internal.Parcelable
import org.cru.godtools.shared.renderer.state.internal.Parcelize

@JsExport
@Parcelize
@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
class State internal constructor(
    private val triggeredAnalyticsEvents: MutableMap<String, Int> = mutableMapOf(),
    private val vars: MutableMap<String, List<String>?> = mutableMapOf(),
) : Parcelable {
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

    // region State vars
    private val varsChangeFlow = MutableSharedFlow<String>(extraBufferCapacity = Int.MAX_VALUE)
    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun <T> varsChangeFlow(keys: Collection<String>? = emptyList(), block: (State) -> T) = when {
        keys.isNullOrEmpty() -> flowOf(Unit)
        else -> varsChangeFlow.onSubscription { emit(keys.first()) }.filter { it in keys }.map {}.conflate()
    }.map { block(this) }

    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun getVar(key: String) = vars[key].orEmpty()

    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun setVar(key: String, values: List<String>?) {
        vars[key] = values?.toList()
        varsChangeFlow.tryEmit(key)
    }

    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun addVarValue(key: String, value: String) {
        val values = getVar(key)
        if (!values.contains(value)) setVar(key, (values + value))
    }
    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun removeVarValue(key: String, value: String) {
        val values = getVar(key)
        if (values.contains(value)) setVar(key, values.filterNot { it == value })
    }
    // endregion State vars
}
