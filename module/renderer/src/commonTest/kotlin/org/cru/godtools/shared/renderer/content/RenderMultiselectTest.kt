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
import app.cash.turbine.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.filterIsInstance
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
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

    @Test
    fun `Option - Card - Action - Click - AnalyticsEvents`() = testClickAnalyticsEvents(Option.Style.CARD)
    @Test
    fun `Option - Flat - Action - Click - AnalyticsEvents`() = testClickAnalyticsEvents(Option.Style.FLAT)
    private fun testClickAnalyticsEvents(style: Option.Style) = runComposeUiTest {
        val multiselect = Multiselect(stateName = "test", selectionLimit = 2) {
            listOf(
                Option(
                    it,
                    value = "value1",
                    style = style,
                    analyticsEvents = listOf(AnalyticsEvent("event1", trigger = AnalyticsEvent.Trigger.CLICKED)),
                ) { listOf(Text(it, text = "option 1")) },
                Option(
                    it,
                    value = "value2",
                    style = style,
                    analyticsEvents = listOf(AnalyticsEvent("event2", trigger = AnalyticsEvent.Trigger.CLICKED)),
                ) { listOf(Text(it, text = "option 2")) },
                Option(
                    it,
                    value = "value3",
                    style = style,
                    analyticsEvents = listOf(AnalyticsEvent("event3", trigger = AnalyticsEvent.Trigger.CLICKED)),
                ) { listOf(Text(it, text = "option 3")) },
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

        state.events.filterIsInstance<State.Event.AnalyticsEvent.ContentEvent>().test {
            expectNoEvents()

            onNodeWithText("option 1").performClick()
            assertEquals("event1", awaitItem().event.action)

            onNodeWithText("option 2").performClick()
            assertEquals("event2", awaitItem().event.action)

            // 2 options are already selected, so we shouldn't be able to select a 3rd option
            onNodeWithText("option 3").performClick()
            expectNoEvents()

            onNodeWithText("option 2").performClick()
            assertEquals("event2", awaitItem().event.action)

            onNodeWithText("option 3").performClick()
            assertEquals("event3", awaitItem().event.action)
        }
    }
    // endregion Option - Action - Click
}
