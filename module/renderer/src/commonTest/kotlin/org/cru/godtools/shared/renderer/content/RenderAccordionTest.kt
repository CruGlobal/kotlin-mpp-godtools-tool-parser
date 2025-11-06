package org.cru.godtools.shared.renderer.content

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.generated.resources.Res
import org.cru.godtools.shared.renderer.generated.resources.tool_renderer_accordion_section_action_collapse
import org.cru.godtools.shared.renderer.generated.resources.tool_renderer_accordion_section_action_expand
import org.cru.godtools.shared.tool.parser.model.Accordion
import org.cru.godtools.shared.tool.parser.model.Text
import org.jetbrains.compose.resources.getString

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class, ExperimentalCoroutinesApi::class)
class RenderAccordionTest : BaseRenderContentTest() {
    override val testModel = Accordion(
        invisibleIf = invisibleIf,
        goneIf = goneIf
    ) {
        listOf(
            Accordion.Section(it, header = { Text(it, "Section 1 Header") }) {
                listOf(Text(it, "Section 1 Content"))
            },
            Accordion.Section(it) { listOf(Text(it, "Section 2 Content")) },
            Accordion.Section(it) { listOf(Text(it, "Section 3 Content")) },
        )
    }

    // region Strings
    private val expandIcon by lazy {
        runBlocking { getString(Res.string.tool_renderer_accordion_section_action_expand) }
    }
    private val collapseIcon by lazy {
        runBlocking { getString(Res.string.tool_renderer_accordion_section_action_collapse) }
    }
    // endregion Strings

    // region Semantics Nodes
    override fun SemanticsNodeInteractionsProvider.onModelNode() = onNodeWithTag(TestTagAccordion)
    private fun SemanticsNodeInteractionsProvider.onSectionNode(section: Int) =
        onAllNodesWithTag(TestTagAccordionSection)[section]

    private val sectionIsExpanded =
        SemanticsMatcher.keyNotDefined(SemanticsActions.Expand)
            .and(SemanticsMatcher.keyIsDefined(SemanticsActions.Collapse))
    private val sectionIsCollapsed =
        SemanticsMatcher.keyNotDefined(SemanticsActions.Collapse)
            .and(SemanticsMatcher.keyIsDefined(SemanticsActions.Expand))
    // endregion Semantics Nodes

    // region UI - Section - Content
    @Test
    fun `UI - Section - Content - Hidden When Collapsed`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderContentStack(listOf(testModel), state = state)
            }
        }

        onNodeWithText("Section 1 Content").assertDoesNotExist()
    }

    @Test
    fun `UI - Section - Content - Visible When Expanded`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderContentStack(listOf(testModel), state = state)
            }
        }

        onSectionNode(0).performSemanticsAction(SemanticsActions.Expand)
        onNodeWithText("Section 1 Content").assertExists()
    }
    // endregion UI - Section - Content

    @Test
    fun `Action - Section - Click - Header - Toggles section`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderContentStack(listOf(testModel), state = state)
            }
        }

        assertEquals(emptySet(), state.accordionExpandedSections(testModel.id))
        onSectionNode(0).assert(sectionIsCollapsed)

        onNodeWithText("Section 1 Header").assertExists().performClick()
        assertEquals(setOf(testModel.sections[0].id), state.accordionExpandedSections(testModel.id))
        onSectionNode(0).assert(sectionIsExpanded)

        onNodeWithText("Section 1 Header").assertExists().performClick()
        assertEquals(emptySet(), state.accordionExpandedSections(testModel.id))
        onSectionNode(0).assert(sectionIsCollapsed)
    }

    @Test
    fun `Action - Section - Click - Header Icon - Toggles section`() = runComposeUiTest {
        val accordion = Accordion {
            listOf(
                Accordion.Section(it, header = { Text(it, "Section 1 Header") }) {
                    listOf(Text(it, "Section 1 Content"))
                },
            )
        }
        setContent {
            ProvideTestCompositionLocals {
                RenderContentStack(listOf(accordion), state = state)
            }
        }

        assertEquals(emptySet(), state.accordionExpandedSections(accordion.id))
        onSectionNode(0).assert(sectionIsCollapsed)

        onNodeWithContentDescription(expandIcon).assertExists().performClick()
        assertEquals(setOf(accordion.sections[0].id), state.accordionExpandedSections(accordion.id))
        onSectionNode(0).assert(sectionIsExpanded)

        onNodeWithContentDescription(collapseIcon).assertExists().performClick()
        assertEquals(emptySet(), state.accordionExpandedSections(accordion.id))
        onSectionNode(0).assert(sectionIsCollapsed)
    }

    @Test
    fun `Action - Section - Click - Content - Doesnt toggle section`() = runComposeUiTest {
        state.toggleAccordionSection(testModel.sections[0])
        setContent {
            ProvideTestCompositionLocals {
                RenderContentStack(listOf(testModel), state = state)
            }
        }

        assertEquals(setOf(testModel.sections[0].id), state.accordionExpandedSections(testModel.id))
        onSectionNode(0).assert(sectionIsExpanded)

        onNodeWithText("Section 1 Content").assertExists().assertHasNoClickAction().performClick()
        assertEquals(setOf(testModel.sections[0].id), state.accordionExpandedSections(testModel.id))
        onSectionNode(0).assert(sectionIsExpanded)
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
