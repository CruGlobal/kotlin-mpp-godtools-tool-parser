package org.cru.godtools.shared.tool.state

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import org.ccci.gto.support.androidx.annotation.RestrictTo
import org.ccci.gto.support.androidx.annotation.RestrictToScope
import org.cru.godtools.shared.tool.state.internal.Parcelable
import org.cru.godtools.shared.tool.state.internal.Parcelize
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@Parcelize
@OptIn(ExperimentalObjCRefinement::class)
class State internal constructor(
    private val triggeredAnalyticsEvents: MutableMap<String, Int> = mutableMapOf(),
    private val vars: MutableMap<String, List<String>?> = mutableMapOf(),
) : Parcelable {
    constructor() : this(vars = mutableMapOf())

    // region Analytics Events Tracking
    @HiddenFromObjC
    @RestrictTo(RestrictToScope.LIBRARY_GROUP)
    fun getTriggeredAnalyticsEventsCount(id: String) = triggeredAnalyticsEvents[id] ?: 0
    @HiddenFromObjC
    @RestrictTo(RestrictToScope.LIBRARY_GROUP)
    fun recordTriggeredAnalyticsEvent(id: String) {
        triggeredAnalyticsEvents[id] = (triggeredAnalyticsEvents[id] ?: 0) + 1
    }
    // endregion Analytics Events Tracking

    // region State vars
    private val varsChangeFlow = MutableSharedFlow<String>(extraBufferCapacity = Int.MAX_VALUE)
    @HiddenFromObjC
    @RestrictTo(RestrictToScope.LIBRARY_GROUP)
    fun <T> varsChangeFlow(keys: Collection<String>? = emptyList(), block: (State) -> T) = when {
        keys.isNullOrEmpty() -> flowOf(Unit)
        else -> varsChangeFlow.filter { it in keys }.map {}.onStart { emit(Unit) }.conflate()
    }.map { block(this) }

    @HiddenFromObjC
    @RestrictTo(RestrictToScope.LIBRARY_GROUP)
    fun getVar(key: String) = vars[key].orEmpty()

    @HiddenFromObjC
    @RestrictTo(RestrictToScope.LIBRARY_GROUP)
    fun setVar(key: String, values: List<String>?) {
        vars[key] = values?.toList()
        varsChangeFlow.tryEmit(key)
    }

    @HiddenFromObjC
    @RestrictTo(RestrictToScope.LIBRARY_GROUP)
    fun addVarValue(key: String, value: String) {
        val values = getVar(key)
        if (!values.contains(value)) setVar(key, (values + value))
    }
    @HiddenFromObjC
    @RestrictTo(RestrictToScope.LIBRARY_GROUP)
    fun removeVarValue(key: String, value: String) {
        val values = getVar(key)
        if (values.contains(value)) setVar(key, values.filterNot { it == value })
    }
    // endregion State vars
}
