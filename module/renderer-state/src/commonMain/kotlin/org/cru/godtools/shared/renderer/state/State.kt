package org.cru.godtools.shared.renderer.state

import androidx.annotation.RestrictTo
import io.fluidsonic.locale.Locale
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName
import kotlin.native.HiddenFromObjC
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import org.ccci.gto.android.common.parcelize.Parcelable
import org.ccci.gto.android.common.parcelize.Parcelize
import org.cru.godtools.shared.common.model.Uri
import org.cru.godtools.shared.tool.parser.expressions.ExpressionContext
import org.cru.godtools.shared.tool.parser.expressions.SimpleExpressionContext
import org.cru.godtools.shared.tool.parser.model.Accordion
import org.cru.godtools.shared.tool.parser.model.EventId
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent as ContentAnalyticsEvent

@JsExport
@Parcelize
@OptIn(ExperimentalJsExport::class, ExperimentalObjCRefinement::class)
class State internal constructor(
    private val triggeredAnalyticsEvents: MutableMap<String, Int> = mutableMapOf(),
    private val vars: MutableMap<String, List<String>?> = mutableMapOf(),
    private val accordionExpandedSections: MutableMap<String, List<String>> = mutableMapOf(),
    private val formFieldValues: MutableMap<String?, String?> = mutableMapOf(),
    private val formFieldValidation: MutableMap<String?, Boolean> = mutableMapOf(),
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

    fun triggerContentEvents(events: List<EventId>) {
        coroutineScope.launch { events.flatMap { it.resolve(this@State) }.forEach { _contentEvents.emit(it) } }
    }
    // endregion Content Events

    // region Events
    sealed class Event {
        data class OpenUrl(val url: Uri) : Event()
        sealed class AnalyticsEvent : Event() {
            data class ScreenView(val tool: String?, val locale: Locale?, val screenName: String) : AnalyticsEvent()
            data class ContentEvent(val event: ContentAnalyticsEvent) : AnalyticsEvent()
        }
        data class SubmitForm(val fields: Map<String, String>) : Event()
        data class OpenTip(val tipId: String) : Event()
    }

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = Int.MAX_VALUE)
    val events = _events.asSharedFlow()

    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun triggerEvent(event: Event) = _events.tryEmit(event)

    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun triggerOpenUrlEvent(url: Uri) = _events.tryEmit(Event.OpenUrl(url))

    // region Analytics Events
    internal fun recordTriggeredAnalyticsEvent(event: ContentAnalyticsEvent) {
        triggeredAnalyticsEvents[event.id] = (triggeredAnalyticsEvents[event.id] ?: 0) + 1
    }
    @Suppress("NullableBooleanElvis")
    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun shouldTriggerAnalyticsEvent(event: ContentAnalyticsEvent) =
        event.limit?.let { (triggeredAnalyticsEvents[event.id] ?: 0) < it } ?: true

    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun triggerAnalyticsEvent(event: ContentAnalyticsEvent) {
        if (!shouldTriggerAnalyticsEvent(event)) return
        recordTriggeredAnalyticsEvent(event)
        _events.tryEmit(Event.AnalyticsEvent.ContentEvent(event))
    }
    // endregion Analytics Events
    // endregion Events

    // region Accordion State
    private val accordionExpandedSectionsChange = MutableSharedFlow<String>(extraBufferCapacity = Int.MAX_VALUE)
    private fun accordionExpandedSectionsChangeFlow(accordionId: String) =
        accordionExpandedSectionsChange.onSubscription { emit(accordionId) }.filter { it == accordionId }

    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun accordionExpandedSectionsFlow(accordionId: String) = accordionExpandedSectionsChangeFlow(accordionId)
        .map { accordionExpandedSections[accordionId]?.toSet().orEmpty() }
        .distinctUntilChanged()

    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun toggleAccordionSection(section: Accordion.Section) {
        val accordionId = section.accordion.id
        val sectionId = section.id
        val currentSections = accordionExpandedSections[accordionId].orEmpty()
        accordionExpandedSections[accordionId] = when {
            sectionId in currentSections -> currentSections - sectionId
            else -> listOf(sectionId)
        }
        accordionExpandedSectionsChange.tryEmit(accordionId)
    }
    // endregion Accordion State

    // region Form State
    private val formFieldChange = MutableSharedFlow<String?>(extraBufferCapacity = Int.MAX_VALUE)
    private fun formFieldChangeFlow(id: String?) = formFieldChange.onSubscription { emit(id) }.filter { it == id }

    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun isFormFieldValidationEnabled(id: String?) = formFieldValidation[id] ?: false
    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun isFormFieldValidationEnabledFlow(id: String?) =
        formFieldChangeFlow(id).map { isFormFieldValidationEnabled(id) }.distinctUntilChanged()
    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun toggleFormFieldValidation(id: String?, validate: Boolean) {
        formFieldValidation[id] = validate
        formFieldChange.tryEmit(id)
    }

    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun formFieldValue(id: String?) = formFieldValues[id]
    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun formFieldValueFlow(id: String?) = formFieldChangeFlow(id).map { formFieldValues[id] }.distinctUntilChanged()
    @HiddenFromObjC
    @JsExport.Ignore
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun updateFormFieldValue(id: String?, value: String) {
        formFieldValues[id] = value
        formFieldChange.tryEmit(id)
    }
    // endregion Form State

    // region Tips
    val showTips = MutableStateFlow(false)
    // endregion Tips
}
