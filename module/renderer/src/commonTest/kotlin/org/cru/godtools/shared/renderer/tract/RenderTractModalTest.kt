package org.cru.godtools.shared.renderer.tract

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.BaseRendererTest
import org.cru.godtools.shared.tool.parser.model.EventId
import org.cru.godtools.shared.tool.parser.model.tract.Modal

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTestApi::class)
class RenderTractModalTest : BaseRendererTest() {
    private var onDismissCalled = 0

    @Test
    fun `Event - onDismiss - dismissListener content event`() = runComposeUiTest {
        val event1 = EventId(name = "event1")
        val event2 = EventId(name = "event2")
        val modal = Modal(dismissListeners = setOf(event2))

        setContent {
            ProvideTestCompositionLocals {
                RenderTractModal(
                    modal,
                    state = state,
                    onDismiss = { onDismissCalled++ }
                )
            }
        }

        assertEquals(0, onDismissCalled)

        state.triggerContentEvents(listOf(event1))
        testScope.runCurrent()
        assertEquals(0, onDismissCalled)

        state.triggerContentEvents(listOf(event2))
        testScope.runCurrent()
        assertEquals(1, onDismissCalled)
    }
}
