package org.cru.godtools.shared.renderer.content

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.model.EventId
import org.cru.godtools.shared.tool.parser.model.Tabs
import org.cru.godtools.shared.tool.parser.model.Text

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class RenderTabsTest : BaseRenderContentTest() {
    private companion object {
        val TAB_1_EVENT_LISTENER = EventId(name = "tab_1_listener")
        val TAB_2_EVENT_LISTENER = EventId(name = "tab_2_listener")
    }

    override val testModel = Tabs(
        tabs = {
            listOf(
                Tabs.Tab(
                    parent = it,
                    label = Text(text = "1"),
                    listeners = setOf(TAB_1_EVENT_LISTENER)
                ),
                Tabs.Tab(
                    parent = it,
                    label = Text(text = "2"),
                    listeners = setOf(TAB_2_EVENT_LISTENER)
                )
            )
        },
        invisibleIf = invisibleIf,
        goneIf = goneIf
    )

    override fun SemanticsNodeInteractionsProvider.onModelNode() = onNodeWithTag(TestTagTabs)

    @Test
    fun `Action - Second Tab Is Selected When Clicked`() = runComposeUiTest {
        setContent {
            RenderContentStack(
                listOf(testModel),
                state = state
            )
        }

        val firstTab = 0
        val secondTab = 1

        onAllNodesWithTag(TestTagTab)[firstTab].assertIsSelected()
        onAllNodesWithTag(TestTagTab)[secondTab].assertIsNotSelected()

        onAllNodesWithTag(TestTagTab)[secondTab].assertExists().performClick()

        onAllNodesWithTag(TestTagTab)[firstTab].assertIsNotSelected()
        onAllNodesWithTag(TestTagTab)[secondTab].assertIsSelected()
    }

    @Test
    fun `Action - Second Tab Is Selected On Content Event`() = runComposeUiTest {
        setContent {
            RenderContentStack(
                listOf(testModel),
                state = state
            )
        }

        val firstTab = 0
        val secondTab = 1

        onAllNodesWithTag(TestTagTab)[firstTab].assertIsSelected()
        onAllNodesWithTag(TestTagTab)[secondTab].assertIsNotSelected()

        testScope.runTest {
            state.triggerContentEvents(listOf(TAB_2_EVENT_LISTENER))

            testScope.testScheduler.runCurrent()

            onAllNodesWithTag(TestTagTab)[firstTab].assertIsNotSelected()
            onAllNodesWithTag(TestTagTab)[secondTab].assertIsSelected()
        }
    }
}
