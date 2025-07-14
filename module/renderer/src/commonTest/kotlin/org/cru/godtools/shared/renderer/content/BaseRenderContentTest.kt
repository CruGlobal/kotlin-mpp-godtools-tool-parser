package org.cru.godtools.shared.renderer.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.hasNoClickAction
import androidx.compose.ui.test.isNotEnabled
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.testing.TestLifecycleOwner
import app.cash.turbine.turbineScope
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.cru.godtools.shared.renderer.TestConstants
import org.cru.godtools.shared.renderer.TestResources
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.renderer.util.ProvideRendererServices
import org.cru.godtools.shared.tool.parser.model.Clickable
import org.cru.godtools.shared.tool.parser.model.Content
import org.cru.godtools.shared.tool.parser.model.EventId

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTestApi::class)
abstract class BaseRenderContentTest {
    internal companion object {
        private const val GONE = "gone"
        private const val INVISIBLE = "invisible"
    }

    protected val state = State()

    protected val lifecycleOwner = TestLifecycleOwner(Lifecycle.State.RESUMED)
    protected val testScope = TestScope()

    @Composable
    protected fun ProvideTestCompositionLocals(content: @Composable () -> Unit) {
        CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner, content = content)
    }

    @BeforeTest
    fun setup() {
        state.setTestCoroutineScope(testScope.backgroundScope)
    }

    protected abstract val testModel: Content
    protected abstract fun SemanticsNodeInteractionsProvider.onModelNode(): SemanticsNodeInteraction

    // region Clickable
    protected val clickableEvents = listOf(EventId(name = "test"), EventId(name = "test2"))
    protected val clickableUrl = TestConstants.TEST_URL

    @Test
    fun `Action - Click - Triggers Clickable`() = runComposeUiTest {
        // short-circuit if we don't have a clickableModel to test
        if (testModel !is Clickable) return@runComposeUiTest

        setContent {
            ProvideTestCompositionLocals {
                ProvideRendererServices(TestResources.fileSystem) {
                    RenderContentStack(
                        listOf(testModel),
                        state = state,
                    )
                }
            }
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
        // short-circuit if we don't have a clickableModel to test
        if (testModel !is Clickable) return@runComposeUiTest

        setContent {
            ProvideTestCompositionLocals {
                ProvideRendererServices(TestResources.fileSystem) {
                    RenderContentStack(
                        listOf(testModel),
                        state = state,
                    )
                }
            }
        }

        onModelNode().assertHasClickAction()
        state.setVar(INVISIBLE, listOf("value"))
        onModelNode().assert(hasNoClickAction() or isNotEnabled())
        state.setVar(INVISIBLE, null)
        onModelNode().assertHasClickAction()
    }
    // endregion Clickable

    // region Visibility
    protected val goneIf = "isSet($GONE)"
    protected val invisibleIf = "isSet($INVISIBLE)"

    @Test
    fun `UI - Visibility - goneIf`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                ProvideRendererServices(TestResources.fileSystem) {
                    RenderContentStack(
                        listOf(testModel),
                        state = state,
                    )
                }
            }
        }

        onModelNode().assertExists()
        state.setVar(GONE, listOf("value"))
        onModelNode().assertDoesNotExist()
        state.setVar(GONE, null)
        onModelNode().assertExists()
    }
    // endregion Visibility
}
