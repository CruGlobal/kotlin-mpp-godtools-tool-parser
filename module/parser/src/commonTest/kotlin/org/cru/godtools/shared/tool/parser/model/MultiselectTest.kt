package org.cru.godtools.shared.tool.parser.model

import app.cash.turbine.test
import com.github.ajalt.colormath.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.ParserConfig
import org.cru.godtools.shared.tool.parser.ParserConfig.Companion.FEATURE_MULTISELECT
import org.cru.godtools.shared.tool.parser.expressions.SimpleExpressionContext
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.internal.color
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Trigger
import org.cru.godtools.shared.tool.parser.model.tips.InlineTip
import org.cru.godtools.shared.tool.parser.model.tips.Tip
import org.cru.godtools.shared.tool.util.assertEquals

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class MultiselectTest : UsesResources() {
    private val expressionCtx by lazy { SimpleExpressionContext() }

    // region parse Multiselect
    @Test
    fun testParseMultiselect() = runTest {
        val multiselect = Multiselect(Manifest(), getTestXmlParser("multiselect.xml"))
        assertEquals("quiz1", multiselect.stateName)
        assertEquals(2, multiselect.columns)
        assertEquals(2, multiselect.selectionLimit)
        assertEquals(3, multiselect.options.size)
        with(multiselect.options[0]) {
            assertEquals(Multiselect.Option.Style.CARD, style)
            assertEquals(TestColors.RED, backgroundColor)
            assertEquals(TestColors.BLUE, selectedColor)
            assertTrue(analyticsEvents.single().isForSystem(AnalyticsEvent.System.FIREBASE))
        }
        with(multiselect.options[1]) {
            assertEquals("answer2", value)
            assertEquals(Multiselect.Option.Style.FLAT, style)
            assertEquals(TestColors.BLUE, backgroundColor)
            assertEquals(TestColors.GREEN, selectedColor)
            assertEquals(1, content.size)
            with(assertIs<Text>(content.single())) {
                assertEquals("Answer 2", text)
            }
        }
    }

    @Test
    fun testParseMultiselectDefaults() = runTest {
        val manifest = Manifest(multiselectOptionSelectedColor = TestColors.random())
        val multiselect = Multiselect(manifest, getTestXmlParser("multiselect_defaults.xml"))
        assertEquals("", multiselect.stateName)
        assertEquals(1, multiselect.columns)
        assertEquals(1, multiselect.selectionLimit)
        assertEquals(1, multiselect.options.size)
        with(multiselect.options.single()) {
            assertEquals(Multiselect.Option.DEFAULT_STYLE, style)
            assertEquals("valueAttr", value)
            assertEquals(manifest.backgroundColor, backgroundColor)
            assertEquals(manifest.multiselectOptionSelectedColor, selectedColor)
            assertTrue(analyticsEvents.isEmpty())
            assertTrue(content.isEmpty())
        }
    }
    // endregion parse Multiselect

    @Test
    fun testIsIgnored() {
        with(Multiselect(Manifest(ParserConfig().withSupportedFeatures(FEATURE_MULTISELECT)))) {
            assertFalse(isIgnored)
        }
        with(Multiselect(Manifest(ParserConfig().withSupportedFeatures()))) {
            assertTrue(isIgnored)
        }
    }

    @Test
    fun testPropertyTips() {
        val manifest = Manifest(tips = { listOf(Tip(it, "tip1"), Tip(it, "tip2"), Tip(it, "tip3"), Tip(it, "tip4")) })
        val multiselect = Multiselect(
            manifest,
            options = {
                listOf(
                    Multiselect.Option(it) { listOf(InlineTip(it, "tip1")) },
                    Multiselect.Option(it) { listOf(InlineTip(it, "tip2"), InlineTip(it, "tip3")) },
                )
            },
        )

        assertEquals(
            listOf(manifest.findTip("tip1"), manifest.findTip("tip2"), manifest.findTip("tip3")),
            multiselect.tips,
        )
    }

    @Test
    fun `Property - children`() {
        val multiselect = Multiselect {
            listOf(
                Multiselect.Option(it) { listOf(Text(it, "text1")) },
                Multiselect.Option(it) { listOf(Text(it, "text2")) },
            )
        }

        assertEquals(2, multiselect.children.size)
        assertEquals(multiselect.options, multiselect.children)
    }

    @Test
    fun testOptionGetAnalyticsEvents() {
        val defaultEvent = AnalyticsEvent(trigger = Trigger.DEFAULT)
        val clickedEvent = AnalyticsEvent(trigger = Trigger.CLICKED)
        val visibleEvent = AnalyticsEvent(trigger = Trigger.VISIBLE)
        val option = Multiselect.Option(
            analyticsEvents = listOf(defaultEvent, clickedEvent, visibleEvent)
        )

        assertEquals(listOf(defaultEvent, clickedEvent), option.getAnalyticsEvents(Trigger.CLICKED))
        assertFailsWith(IllegalStateException::class) { option.getAnalyticsEvents(Trigger.DEFAULT) }
        assertFailsWith(IllegalStateException::class) { option.getAnalyticsEvents(Trigger.VISIBLE) }
    }

    // region *isSelected*()
    @Test
    fun testOptionIsSelected() {
        val multiselect = Multiselect { it.options(2) }

        multiselect.options[0].toggleSelected(expressionCtx)
        assertTrue(multiselect.options[0].isSelected(expressionCtx))
        assertFalse(multiselect.options[1].isSelected(expressionCtx))
    }

    @Test
    fun testOptionIsSelectedFlow() = runTest {
        val multiselect = Multiselect { it.options(2) }
        multiselect.options[0].isSelectedFlow(expressionCtx).test {
            assertFalse(awaitItem(), "Initially not selected")

            multiselect.options[0].toggleSelected(expressionCtx)
            assertTrue(awaitItem(), "Toggled this option to true")

            multiselect.options[1].toggleSelected(expressionCtx)
            assertFalse(awaitItem(), "Toggled other option to true")

            multiselect.options[1].toggleSelected(expressionCtx)
            expectNoEvents() // Toggled other option to false

            expressionCtx.setVar("other", listOf("test"))
            expectNoEvents() // Set unrelated state value
        }
    }

    @Test
    fun testOptionWatchIsSelected() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        var selected = true
        val multiselect = Multiselect { it.options(2) }
        val option = multiselect.options[0]

        val watcher = option.watchIsSelected(expressionCtx) { selected = it }
        assertFalse(selected, "Initially not selected")

        multiselect.options[0].toggleSelected(expressionCtx)
        assertTrue(selected, "Toggled this option to true")

        multiselect.options[1].toggleSelected(expressionCtx)
        assertFalse(selected, "Toggled other option to true")

        multiselect.options[1].toggleSelected(expressionCtx)
        assertFalse(selected, "Toggled other option to false")

        expressionCtx.setVar("other", listOf("test"))
        assertFalse(selected, "Set unrelated state value")

        watcher.close()
        multiselect.options[0].toggleSelected(expressionCtx)
        assertFalse(selected, "Watcher was already closed")
    }
    // endregion *isSelected*()

    // region Option.toggleSelected()
    @Test
    fun testOptionToggleSelectedSingleSelection() {
        with(Multiselect(selectionLimit = 1) { it.options(2) }) {
            // initial state
            assertFalse(options[0].isSelected(expressionCtx))
            assertFalse(options[1].isSelected(expressionCtx))

            // select first
            assertTrue(options[0].toggleSelected(expressionCtx))
            assertTrue(options[0].isSelected(expressionCtx))
            assertFalse(options[1].isSelected(expressionCtx))

            // select second
            assertTrue(options[1].toggleSelected(expressionCtx))
            assertFalse(options[0].isSelected(expressionCtx))
            assertTrue(options[1].isSelected(expressionCtx))

            // unselect second
            assertTrue(options[1].toggleSelected(expressionCtx))
            assertFalse(options[0].isSelected(expressionCtx))
            assertFalse(options[1].isSelected(expressionCtx))
        }
    }

    @Test
    fun testOptionToggleSelectedMultipleSelections() {
        with(Multiselect(selectionLimit = 2) { it.options(3) }) {
            // initial state
            assertFalse(options[0].isSelected(expressionCtx))
            assertFalse(options[1].isSelected(expressionCtx))
            assertFalse(options[2].isSelected(expressionCtx))

            // select first
            assertTrue(options[0].toggleSelected(expressionCtx))
            assertTrue(options[0].isSelected(expressionCtx))
            assertFalse(options[1].isSelected(expressionCtx))
            assertFalse(options[2].isSelected(expressionCtx))

            // select second
            assertTrue(options[1].toggleSelected(expressionCtx))
            assertTrue(options[0].isSelected(expressionCtx))
            assertTrue(options[1].isSelected(expressionCtx))
            assertFalse(options[2].isSelected(expressionCtx))

            // try to select third
            assertFalse(options[2].toggleSelected(expressionCtx))
            assertTrue(options[0].isSelected(expressionCtx))
            assertTrue(options[1].isSelected(expressionCtx))
            assertFalse(options[2].isSelected(expressionCtx))

            // unselect first
            assertTrue(options[0].toggleSelected(expressionCtx))
            assertFalse(options[0].isSelected(expressionCtx))
            assertTrue(options[1].isSelected(expressionCtx))
            assertFalse(options[2].isSelected(expressionCtx))
        }
    }

    @Test
    fun `Option - toggleSelected - sets ExpressionContext var`() {
        val multiselect = Multiselect(stateName = "varName", selectionLimit = 2) { it.options(3) }
        assertEquals(emptyList(), expressionCtx.getVar("varName"))

        multiselect.options[2].toggleSelected(expressionCtx)
        assertEquals(setOf("2"), expressionCtx.getVar("varName").toSet())

        multiselect.options[0].toggleSelected(expressionCtx)
        assertEquals(setOf("2", "0"), expressionCtx.getVar("varName").toSet())

        multiselect.options[1].toggleSelected(expressionCtx)
        assertEquals(setOf("2", "0"), expressionCtx.getVar("varName").toSet())

        multiselect.options[2].toggleSelected(expressionCtx)
        assertEquals(setOf("0"), expressionCtx.getVar("varName").toSet())
    }
    // endregion Option.toggleSelected()

    @Test
    fun testOptionBackgroundColorFallback() {
        val parent = Manifest(multiselectOptionBackgroundColor = TestColors.random())
        with(Multiselect.Option(Multiselect(parent))) {
            assertEquals(parent.multiselectOptionBackgroundColor, backgroundColor)
        }

        val multiselectBackgroundColor = TestColors.random()
        val multiselect = Multiselect(parent, optionBackgroundColor = multiselectBackgroundColor)
        with(Multiselect.Option(multiselect)) {
            assertEquals(multiselectBackgroundColor, backgroundColor)
        }

        val optionBackgroundColor = TestColors.random()
        with(Multiselect.Option(multiselect, backgroundColor = optionBackgroundColor)) {
            assertEquals(optionBackgroundColor, backgroundColor)
        }
    }

    @Test
    fun testOptionSelectedColorFallback() {
        val parent = object : BaseModel(), Styles {
            override var primaryColor = color(254, 0, 0, 0.5)
            override var multiselectOptionSelectedColor: Color? = null
        }

        // 40% lighter primary color w/ 100% alpha
        with(Multiselect.Option(Multiselect(parent))) {
            assertEquals(color(255, 203, 203, 1.0), selectedColor)
        }

        // 40% lighter of white primary color should still be white
        parent.primaryColor = WHITE
        with(Multiselect.Option(Multiselect(parent))) {
            assertEquals(WHITE, selectedColor)
        }

        parent.multiselectOptionSelectedColor = TestColors.random()
        with(Multiselect.Option(Multiselect(parent))) {
            assertEquals(parent.multiselectOptionSelectedColor, selectedColor)
        }

        val multiselectSelectedColor = TestColors.random()
        val multiselect = Multiselect(parent, optionSelectedColor = multiselectSelectedColor)
        with(Multiselect.Option(multiselect)) {
            assertEquals(multiselectSelectedColor, selectedColor)
        }

        val optionSelectedColor = TestColors.random()
        with(Multiselect.Option(multiselect, selectedColor = optionSelectedColor)) {
            assertEquals(optionSelectedColor, selectedColor)
        }
    }

    @Test
    fun `Option - Property - children`() {
        val option = Multiselect.Option {
            listOf(
                Text(it, "text1"),
                Text(it, "text2"),
            )
        }

        assertEquals(2, option.children.size)
        assertEquals(option.content, option.children)
    }

    private fun Multiselect.options(count: Int = 2) = List(count) { Multiselect.Option(this, value = "$it") }
}
