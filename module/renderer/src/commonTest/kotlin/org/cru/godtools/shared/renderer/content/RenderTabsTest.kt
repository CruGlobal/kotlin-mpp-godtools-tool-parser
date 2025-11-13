package org.cru.godtools.shared.renderer.content

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import app.cash.turbine.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.test.runCurrent
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.EventId
import org.cru.godtools.shared.tool.parser.model.Tabs
import org.cru.godtools.shared.tool.parser.model.Text

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class, ExperimentalCoroutinesApi::class)
class RenderTabsTest : BaseRenderContentTest() {
    private companion object {
        val TAB_1_EVENT_LISTENER = EventId(name = "tab_1_listener")
        val TAB_2_EVENT_LISTENER = EventId(name = "tab_2_listener")
    }

    override val testModel = Tabs(invisibleIf = invisibleIf, goneIf = goneIf) {
        listOf(
            Tabs.Tab(
                parent = it,
                label = { Text(text = "1") },
                listeners = setOf(TAB_1_EVENT_LISTENER),
            ),
            Tabs.Tab(
                parent = it,
                label = { Text(text = "2") },
                listeners = setOf(TAB_2_EVENT_LISTENER),
            ),
        )
    }

    override fun SemanticsNodeInteractionsProvider.onModelNode() = onNodeWithTag(TestTagTabs)
    private fun SemanticsNodeInteractionsProvider.onTabNode(index: Int) = onAllNodesWithTag(TestTagTab)[index]

    @Test
    fun `Action - Second Tab Is Selected When Clicked`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderContentStack(
                    listOf(testModel),
                    state = state
                )
            }
        }

        onTabNode(0).assertIsSelected()
        onTabNode(1).assertIsNotSelected()

        onTabNode(1).assertExists().performClick()

        onTabNode(0).assertIsNotSelected()
        onTabNode(1).assertIsSelected()
    }

    @Test
    fun `Action - Second Tab Is Selected On Content Event`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderContentStack(
                    listOf(testModel),
                    state = state
                )
            }
        }

        onTabNode(0).assertIsSelected()
        onTabNode(1).assertIsNotSelected()

        state.triggerContentEvents(listOf(TAB_2_EVENT_LISTENER))
        testScope.runCurrent()

        onTabNode(0).assertIsNotSelected()
        onTabNode(1).assertIsSelected()
    }

    // region Action - Click - AnalyticsEvents
    @Test
    fun `Action - Click - Trigger Analytics Event`() = runComposeUiTest {
        val tabs = Tabs {
            listOf(
                Tabs.Tab(
                    it,
                    analyticsEvents = listOf(AnalyticsEvent("tab1_click")),
                    label = { Text(text = "Tab 1") },
                ),
                Tabs.Tab(
                    it,
                    analyticsEvents = listOf(AnalyticsEvent("tab2_click")),
                    label = { Text(text = "Tab 2") },
                ),
            )
        }

        setContent {
            ProvideTestCompositionLocals {
                RenderContentStack(
                    listOf(tabs),
                    state = state
                )
            }
        }

        state.events.filterIsInstance<State.Event.AnalyticsEvent.ContentEvent>().test {
            expectNoEvents()

            onNodeWithText("Tab 2").performClick()
            assertEquals("tab2_click", awaitItem().event.action)

            onNodeWithText("Tab 1").performClick()
            assertEquals("tab1_click", awaitItem().event.action)
        }
    }
    // endregion Action - Click - AnalyticsEvents
}
