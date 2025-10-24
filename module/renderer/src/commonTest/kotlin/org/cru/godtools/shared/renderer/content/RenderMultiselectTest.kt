package org.cru.godtools.shared.renderer.content

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasNoClickAction
import androidx.compose.ui.test.isNotEnabled
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.model.Multiselect
import org.cru.godtools.shared.tool.parser.model.Multiselect.Option
import org.cru.godtools.shared.tool.parser.model.Text

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class RenderMultiselectTest : BaseRenderContentTest() {
    override val testModel = Multiselect(invisibleIf = invisibleIf, goneIf = goneIf)
    override fun SemanticsNodeInteractionsProvider.onModelNode() = onNodeWithTag(TestTagMultiselect)

    // region Option - Action - Click
    @Test
    fun `Option - Card - Action - Click - selectionLimit=1`() = testClickSelectionLimit1(Option.Style.CARD)
    @Test
    fun `Option - Flat - Action - Click - selectionLimit=1`() = testClickSelectionLimit1(Option.Style.FLAT)
    private fun testClickSelectionLimit1(style: Option.Style) = runComposeUiTest {
        val multiselect = Multiselect(stateName = "test", selectionLimit = 1) {
            listOf(
                Option(it, value = "value1", style = style) { listOf(Text(it, text = "option 1")) },
                Option(it, value = "value2", style = style) { listOf(Text(it, text = "option 2")) },
                Option(it, value = "value3", style = style) { listOf(Text(it, text = "option 3")) },
            )
        }

        setContent {
            ProvideTestCompositionLocals {
                RenderContentStack(
                    listOf(multiselect),
                    state = state,
                )
            }
        }

        assertEquals(emptyList(), state.getVar("test"))

        onNodeWithText("option 1").performClick()
        assertEquals(listOf("value1"), state.getVar("test"))

        onNodeWithText("option 2").performClick()
        assertEquals(listOf("value2"), state.getVar("test"))

        onNodeWithText("option 2").performClick()
        assertEquals(emptyList(), state.getVar("test"))
    }

    @Test
    fun `Option - Card - Action - Click - selectionLimit=2`() = testClickSelectionLimit2(Option.Style.CARD)
    @Test
    fun `Option - Flat - Action - Click - selectionLimit=2`() = testClickSelectionLimit2(Option.Style.FLAT)
    private fun testClickSelectionLimit2(style: Option.Style) = runComposeUiTest {
        val multiselect = Multiselect(stateName = "test", selectionLimit = 2) {
            listOf(
                Option(it, value = "value1", style = style) { listOf(Text(it, text = "option 1")) },
                Option(it, value = "value2", style = style) { listOf(Text(it, text = "option 2")) },
                Option(it, value = "value3", style = style) { listOf(Text(it, text = "option 3")) },
            )
        }

        setContent {
            ProvideTestCompositionLocals {
                RenderContentStack(
                    listOf(multiselect),
                    state = state,
                )
            }
        }

        assertEquals(emptyList(), state.getVar("test"))
        onNodeWithText("option 1").assertIsEnabled().assertHasClickAction()
        onNodeWithText("option 2").assertIsEnabled().assertHasClickAction()
        onNodeWithText("option 3").assertIsEnabled().assertHasClickAction()

        onNodeWithText("option 1").performClick()
        assertEquals(listOf("value1"), state.getVar("test"))
        onNodeWithText("option 1").assertIsEnabled().assertHasClickAction()
        onNodeWithText("option 2").assertIsEnabled().assertHasClickAction()
        onNodeWithText("option 3").assertIsEnabled().assertHasClickAction()

        onNodeWithText("option 2").performClick()
        assertEquals(setOf("value1", "value2"), state.getVar("test").toSet())
        onNodeWithText("option 1").assertIsEnabled().assertHasClickAction()
        onNodeWithText("option 2").assertIsEnabled().assertHasClickAction()
        onNodeWithText("option 3").assertExists().assert(hasNoClickAction() or isNotEnabled())

        onNodeWithText("option 1").performClick()
        assertEquals(listOf("value2"), state.getVar("test"))
        onNodeWithText("option 1").assertIsEnabled().assertHasClickAction()
        onNodeWithText("option 2").assertIsEnabled().assertHasClickAction()
        onNodeWithText("option 3").assertIsEnabled().assertHasClickAction()
    }
    // endregion Option - Action - Click
}
