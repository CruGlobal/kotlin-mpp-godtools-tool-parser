package org.cru.godtools.shared.renderer.content

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher.Companion.expectValue
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.model.Tabs
import org.cru.godtools.shared.tool.parser.model.Text

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class RenderTabsTest : BaseRenderContentTest() {
    private companion object {
        val MATCHER_TAB_IS_SELECTED = expectValue(TabIsSelected, true)
        val MATCHER_TAB_IS_NOT_SELECTED = expectValue(TabIsSelected, false)
    }

    override val testModel = Tabs(
        tabs = {
            listOf(
                Tabs.Tab(
                    parent = it,
                    label = Text(text = "1")
                ),
                Tabs.Tab(
                    parent = it,
                    label = Text(text = "2")
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

        onNodeWithTag(RenderTabs.getTabTestTag(firstTab)).assert(MATCHER_TAB_IS_SELECTED)
        onNodeWithTag(RenderTabs.getTabTestTag(secondTab)).assert(MATCHER_TAB_IS_NOT_SELECTED)

        onNodeWithTag(RenderTabs.getTabTestTag(secondTab)).performClick()
        onNodeWithTag(RenderTabs.getTabTestTag(firstTab)).assert(MATCHER_TAB_IS_NOT_SELECTED)
        onNodeWithTag(RenderTabs.getTabTestTag(secondTab)).assert(MATCHER_TAB_IS_SELECTED)
    }
}
