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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
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
                label = Text(text = "1"),
                listeners = setOf(TAB_1_EVENT_LISTENER)
            ),
            Tabs.Tab(
                parent = it,
                label = Text(text = "2"),
                listeners = setOf(TAB_2_EVENT_LISTENER)
            )
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
}
