package org.cru.godtools.shared.renderer.lesson

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Manifest

data object LessonScreen {
    sealed interface UiState : CircuitUiState {
        val manifest: Manifest? get() = null
        val eventSink: (UiEvent) -> Unit

        data class Loaded(
            override val manifest: Manifest,
            val state: State,
            val lessonPager: LessonPagerState = LessonPagerState(manifest = manifest),
            override val eventSink: (UiEvent) -> Unit = {},
        ) : UiState
        data class Loading(val progress: Float? = null, override val eventSink: (UiEvent) -> Unit = {}) : UiState
        data class Missing(override val eventSink: (UiEvent) -> Unit = {}) : UiState
        data class Offline(override val eventSink: (UiEvent) -> Unit = {}) : UiState
    }

    sealed interface UiEvent : CircuitUiEvent {
        data object CloseLesson : UiEvent
    }
}
