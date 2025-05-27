package org.cru.godtools.shared.renderer.state

import androidx.annotation.RestrictTo
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.native.HiddenFromObjC
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import org.cru.godtools.shared.common.model.Uri
import org.cru.godtools.shared.renderer.state.internal.Parcelable
import org.cru.godtools.shared.renderer.state.internal.Parcelize
import org.cru.godtools.shared.tool.parser.model.EventId

@JsExport
@Parcelize
@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
class State internal constructor(
    private val triggeredAnalyticsEvents: MutableMap<String, Int> = mutableMapOf(),
    private val vars: MutableMap<String, List<String>?> = mutableMapOf(),
) : Parcelable {
    @JsName("createState")
    constructor() : this(vars = mutableMapOf())

    private var coroutineScope = CoroutineScope(EmptyCoroutineContext)
    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.TESTS)
    fun setTestCoroutineScope(scope: CoroutineScope) {
        coroutineScope = scope
    }

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

    // region Content Events
    private val _contentEvents = MutableSharedFlow<EventId>()
    val contentEvents = _contentEvents.asSharedFlow()

    internal fun resolveContentEvent(eventId: EventId) = when (eventId.namespace) {
        EventId.NAMESPACE_STATE -> getVar(eventId.name).map { EventId(name = it) }
        else -> listOf(eventId)
    }

    fun triggerContentEvents(events: List<EventId>) {
        coroutineScope.launch { events.flatMap { resolveContentEvent(it) }.forEach { _contentEvents.emit(it) } }
    }
    // endregion Content Events

    // region Events
    sealed class Event {
        data class OpenUrl(val url: Uri) : Event()
    }

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = Int.MAX_VALUE)
    val events = _events.asSharedFlow()

    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun triggerOpenUrlEvent(url: Uri) = _events.tryEmit(Event.OpenUrl(url))
    // endregion Events
}
