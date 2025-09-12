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
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.model.Accordion

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class, ExperimentalCoroutinesApi::class)
class RenderAccordionTest : BaseRenderContentTest() {
    override val testModel = Accordion(
        invisibleIf = invisibleIf,
        goneIf = goneIf
    ) {
        listOf(
            Accordion.Section(it),
            Accordion.Section(it),
            Accordion.Section(it)
        )
    }

    override fun SemanticsNodeInteractionsProvider.onModelNode() = onNodeWithTag(TestTagAccordion)
    private fun SemanticsNodeInteractionsProvider.onSectionNode(index: Int) =
        onAllNodesWithTag(TestTagAccordionSection)[index]
    @Test
    fun `Action - First Section Is Selected When Clicked`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderContentStack(
                    listOf(testModel),
                    state = state
                )
            }
        }

        onSectionNode(0).assertIsNotSelected()
        onSectionNode(1).assertIsNotSelected()
        onSectionNode(2).assertIsNotSelected()

        onSectionNode(0).assertExists().performClick()

        onSectionNode(0).assertIsSelected()
        onSectionNode(1).assertIsNotSelected()
        onSectionNode(2).assertIsNotSelected()
    }
}
