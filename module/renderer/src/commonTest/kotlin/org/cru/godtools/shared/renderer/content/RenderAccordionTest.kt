package org.cru.godtools.shared.renderer.content

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
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

    // region Semantics Nodes
    override fun SemanticsNodeInteractionsProvider.onModelNode() = onNodeWithTag(TestTagAccordion)
    private fun SemanticsNodeInteractionsProvider.onSectionNode(index: Int) =
        onAllNodesWithTag(TestTagAccordionSection)[index]
    private val sectionIsExpanded =
        SemanticsMatcher.keyNotDefined(SemanticsActions.Expand)
            .and(SemanticsMatcher.keyIsDefined(SemanticsActions.Collapse))
    private val sectionIsCollapsed =
        SemanticsMatcher.keyNotDefined(SemanticsActions.Collapse)
            .and(SemanticsMatcher.keyIsDefined(SemanticsActions.Expand))
    // endregion Semantics Nodes

    @Test
    fun `Action - Section - Click - Toggles section`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderContentStack(listOf(testModel), state = state)
            }
        }

        assertEquals(emptySet(), state.accordionExpandedSections(testModel.id))
        onSectionNode(0).assert(sectionIsCollapsed)
        onSectionNode(1).assert(sectionIsCollapsed)
        onSectionNode(2).assert(sectionIsCollapsed)

        onSectionNode(0).assertExists().performClick()
        assertEquals(setOf(testModel.sections[0].id), state.accordionExpandedSections(testModel.id))
        onSectionNode(0).assert(sectionIsExpanded)
        onSectionNode(1).assert(sectionIsCollapsed)
        onSectionNode(2).assert(sectionIsCollapsed)

        onSectionNode(0).assertExists().performClick()
        assertEquals(emptySet(), state.accordionExpandedSections(testModel.id))
        onSectionNode(0).assert(sectionIsCollapsed)
        onSectionNode(1).assert(sectionIsCollapsed)
        onSectionNode(2).assert(sectionIsCollapsed)
    }

    @Test
    fun `Action - Section - Accessibility - Expand and Collapse`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderContentStack(listOf(testModel), state = state)
            }
        }

        assertEquals(emptySet(), state.accordionExpandedSections(testModel.id))

        onSectionNode(0).performSemanticsAction(SemanticsActions.Expand)
        assertEquals(setOf(testModel.sections[0].id), state.accordionExpandedSections(testModel.id))

        onSectionNode(0).performSemanticsAction(SemanticsActions.Collapse)
        assertEquals(emptySet(), state.accordionExpandedSections(testModel.id))
    }
}
