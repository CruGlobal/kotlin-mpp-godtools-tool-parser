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
import kotlinx.coroutines.launch
import org.cru.godtools.shared.common.model.Uri
import org.cru.godtools.shared.renderer.state.internal.Parcelable
import org.cru.godtools.shared.renderer.state.internal.Parcelize
import org.cru.godtools.shared.tool.parser.expressions.ExpressionContext
import org.cru.godtools.shared.tool.parser.expressions.SimpleExpressionContext
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.EventId

@JsExport
@Parcelize
@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
class State internal constructor(
    private val triggeredAnalyticsEvents: MutableMap<String, Int> = mutableMapOf(),
    private val vars: MutableMap<String, List<String>?> = mutableMapOf(),
) : Parcelable, ExpressionContext by SimpleExpressionContext(vars) {
    @JsName("createState")
    constructor() : this(vars = mutableMapOf())

    private var coroutineScope = CoroutineScope(EmptyCoroutineContext)
    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.TESTS)
    fun setTestCoroutineScope(scope: CoroutineScope) {
        coroutineScope = scope
    }

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
        data class AnalyticsEventTriggered(val event: AnalyticsEvent) : Event()
    }

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = Int.MAX_VALUE)
    val events = _events.asSharedFlow()

    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun triggerOpenUrlEvent(url: Uri) = _events.tryEmit(Event.OpenUrl(url))

    // region Analytics Events
    internal fun recordTriggeredAnalyticsEvent(event: AnalyticsEvent) {
        triggeredAnalyticsEvents[event.id] = (triggeredAnalyticsEvents[event.id] ?: 0) + 1
    }
    @Suppress("NullableBooleanElvis")
    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun shouldTriggerAnalyticsEvent(event: AnalyticsEvent) =
        event.limit?.let { (triggeredAnalyticsEvents[event.id] ?: 0) < it } ?: true

    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun triggerAnalyticsEvent(event: AnalyticsEvent) {
        if (!shouldTriggerAnalyticsEvent(event)) return
        recordTriggeredAnalyticsEvent(event)
        _events.tryEmit(Event.AnalyticsEventTriggered(event))
    }
    // endregion Analytics Events
    // endregion Events
}
