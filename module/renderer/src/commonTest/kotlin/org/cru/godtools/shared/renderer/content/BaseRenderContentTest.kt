package org.cru.godtools.shared.renderer.content

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import app.cash.turbine.turbineScope
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.cru.godtools.shared.renderer.TestConstants
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Clickable
import org.cru.godtools.shared.tool.parser.model.Content
import org.cru.godtools.shared.tool.parser.model.EventId

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTestApi::class)
abstract class BaseRenderContentTest {
    internal companion object {
        internal const val GONE = "gone"
        internal const val INVISIBLE = "invisible"
    }

    protected val state = State()

    protected val testScope = TestScope()

    @BeforeTest
    fun setup() {
        state.setTestCoroutineScope(testScope.backgroundScope)
    }

    protected abstract val testModel: Content
    protected abstract fun SemanticsNodeInteractionsProvider.onModelNode(): SemanticsNodeInteraction

    // region Clickable
    protected val clickableEvents = listOf(EventId(name = "test"), EventId(name = "test2"))
    protected val clickableUrl = TestConstants.TEST_URL

    protected val goneIf: String? = "isSet($GONE)"
    protected val invisibleIf: String? = "isSet($INVISIBLE)"

    @Test
    fun `Action - Click - Triggers Clickable`() = runComposeUiTest {
        // short-circuit if we don't have a clickableModel to test
        val clickableModel = testModel.takeIf { it is Clickable } ?: return@runComposeUiTest

        setContent {
            RenderContentStack(
                listOf(clickableModel),
                state = state,
            )
        }

        testScope.runTest {
            turbineScope {
                val urlEvents = state.events.filterIsInstance<State.Event.OpenUrl>().testIn(this)
                val contentEvents = state.contentEvents.testIn(this)

                onModelNode().assertExists().performClick()
                clickableEvents.forEach { assertEquals(it, contentEvents.awaitItem()) }
                assertEquals(clickableUrl, urlEvents.awaitItem().url)

                urlEvents.cancel()
                contentEvents.cancel()
            }
        }
    }

    @Test
    fun `Action - Click - Not Clickable if isInvisible`() = runComposeUiTest {
        // DISABLED: making an item invisible clears it's semantics that UI tests rely on to function.
        //           I don't currently know of a workaround to make this work.
        return@runComposeUiTest

        // short-circuit if we don't have a clickableModel to test
        val clickableModel = testModel.takeIf { it is Clickable } ?: return@runComposeUiTest

        setContent {
            RenderContentStack(
                listOf(clickableModel),
                state = state,
            )
        }

        onModelNode().assertIsEnabled()
        state.setVar(INVISIBLE, listOf("value"))
        onModelNode().assertIsNotEnabled()
        state.setVar(INVISIBLE, null)
        onModelNode().assertIsEnabled()
    }
    // endregion Clickable

    @Test
    fun `UI - Visibility - goneIf`() = runComposeUiTest {
        setContent {
            RenderContentStack(
                listOf(testModel),
                state = state,
            )
        }

        onModelNode().assertExists()
        state.setVar(GONE, listOf("value"))
        onModelNode().assertDoesNotExist()
        state.setVar(GONE, null)
        onModelNode().assertExists()
    }
}
