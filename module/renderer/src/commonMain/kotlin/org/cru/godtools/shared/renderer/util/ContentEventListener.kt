package org.cru.godtools.shared.renderer.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.FlowCollector
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.EventId

@Composable
internal fun ContentEventListener(state: State, vararg keys: Any?, collector: FlowCollector<EventId>) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(state, lifecycle, *keys) {
        state.contentEvents
            .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
            .collect(collector)
    }
}
