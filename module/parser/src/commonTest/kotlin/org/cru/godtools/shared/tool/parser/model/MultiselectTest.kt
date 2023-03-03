package org.cru.godtools.shared.tool.parser.model

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.ParserConfig
import org.cru.godtools.shared.tool.parser.ParserConfig.Companion.FEATURE_MULTISELECT
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Trigger
import org.cru.godtools.shared.tool.state.State
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class MultiselectTest : UsesResources() {
    private val state by lazy { State() }

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
            assertTrue(AnalyticsEvent.System.FIREBASE in analyticsEvents.single().systems)
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
        val manifest = Manifest(multiselectOptionSelectedColor = TestColors.RANDOM)
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

    @Test
    fun testIsIgnored() {
        with(Multiselect(Manifest(ParserConfig().withSupportedFeatures(setOf(FEATURE_MULTISELECT))))) {
            assertFalse(isIgnored)
        }
        with(Multiselect(Manifest(ParserConfig().withSupportedFeatures(emptySet())))) {
            assertTrue(isIgnored)
        }
    }

    @Test
    fun testOptionGetAnalyticsEvents() {
        val defaultEvent = AnalyticsEvent(trigger = Trigger.DEFAULT)
        val clickedEvent = AnalyticsEvent(trigger = Trigger.CLICKED)
        val selectedEvent = AnalyticsEvent(trigger = Trigger.SELECTED)
        val visibleEvent = AnalyticsEvent(trigger = Trigger.VISIBLE)
        val option = Multiselect.Option(
            analyticsEvents = listOf(defaultEvent, clickedEvent, selectedEvent, visibleEvent)
        )

        assertEquals(listOf(defaultEvent, clickedEvent), option.getAnalyticsEvents(Trigger.CLICKED))
        assertFailsWith(IllegalStateException::class) { option.getAnalyticsEvents(Trigger.DEFAULT) }
        assertFailsWith(IllegalStateException::class) { option.getAnalyticsEvents(Trigger.SELECTED) }
        assertFailsWith(IllegalStateException::class) { option.getAnalyticsEvents(Trigger.VISIBLE) }
    }

    @Test
    fun testOptionIsSelected() {
        val multiselect = Multiselect { it.options(2) }

        multiselect.options[0].toggleSelected(state)
        assertTrue(multiselect.options[0].isSelected(state))
        assertFalse(multiselect.options[1].isSelected(state))
    }

    @Test
    fun testOptionIsSelectedFlow() = runTest {
        val multiselect = Multiselect { it.options(2) }
        multiselect.options[0].isSelectedFlow(state).test {
            assertFalse(awaitItem(), "Initially not selected")

            multiselect.options[0].toggleSelected(state)
            assertTrue(awaitItem(), "Toggled this option to true")

            multiselect.options[1].toggleSelected(state)
            assertFalse(awaitItem(), "Toggled other option to true")

            multiselect.options[1].toggleSelected(state)
            expectNoEvents() // Toggled other option to false

            state.setVar("other", listOf("test"))
            expectNoEvents() // Set unrelated state value
        }
    }

    @Test
    fun testOptionToggleSelectedSingleSelection() {
        with(Multiselect(selectionLimit = 1) { it.options(2) }) {
            // initial state
            assertFalse(options[0].isSelected(state))
            assertFalse(options[1].isSelected(state))

            // select first
            assertTrue(options[0].toggleSelected(state))
            assertTrue(options[0].isSelected(state))
            assertFalse(options[1].isSelected(state))

            // select second
            assertTrue(options[1].toggleSelected(state))
            assertFalse(options[0].isSelected(state))
            assertTrue(options[1].isSelected(state))

            // unselect second
            assertTrue(options[1].toggleSelected(state))
            assertFalse(options[0].isSelected(state))
            assertFalse(options[1].isSelected(state))
        }
    }

    @Test
    fun testOptionToggleSelectedMultipleSelections() {
        with(Multiselect(selectionLimit = 2) { it.options(3) }) {
            // initial state
            assertFalse(options[0].isSelected(state))
            assertFalse(options[1].isSelected(state))
            assertFalse(options[2].isSelected(state))

            // select first
            assertTrue(options[0].toggleSelected(state))
            assertTrue(options[0].isSelected(state))
            assertFalse(options[1].isSelected(state))
            assertFalse(options[2].isSelected(state))

            // select second
            assertTrue(options[1].toggleSelected(state))
            assertTrue(options[0].isSelected(state))
            assertTrue(options[1].isSelected(state))
            assertFalse(options[2].isSelected(state))

            // try to select third
            assertFalse(options[2].toggleSelected(state))
            assertTrue(options[0].isSelected(state))
            assertTrue(options[1].isSelected(state))
            assertFalse(options[2].isSelected(state))

            // unselect first
            assertTrue(options[0].toggleSelected(state))
            assertFalse(options[0].isSelected(state))
            assertTrue(options[1].isSelected(state))
            assertFalse(options[2].isSelected(state))
        }
    }

    @Test
    fun testMultiselectAffectsEventIdResolution() {
        val eventId = EventId(EVENT_NAMESPACE_STATE, "test")
        val multiselect = Multiselect(stateName = eventId.name, selectionLimit = 2) { it.options(3) }

        multiselect.options[2].toggleSelected(state)
        multiselect.options[0].toggleSelected(state)
        assertEquals(listOf(EventId(name = "2"), EventId(name = "0")), eventId.resolve(state))
    }

    @Test
    fun testOptionBackgroundColorFallback() {
        val parent = object : BaseModel(), Styles {
            override val multiselectOptionBackgroundColor = TestColors.RANDOM
        }
        with(Multiselect.Option(Multiselect(parent))) {
            assertEquals(parent.multiselectOptionBackgroundColor, backgroundColor)
        }

        val multiselectBackgroundColor = TestColors.RANDOM
        val multiselect = Multiselect(parent, optionBackgroundColor = multiselectBackgroundColor)
        with(Multiselect.Option(multiselect)) {
            assertEquals(multiselectBackgroundColor, backgroundColor)
        }

        val optionBackgroundColor = TestColors.RANDOM
        with(Multiselect.Option(multiselect, backgroundColor = optionBackgroundColor)) {
            assertEquals(optionBackgroundColor, backgroundColor)
        }

        // test with nullable receiver
        with(Multiselect.Option(multiselect, backgroundColor = optionBackgroundColor) as Multiselect.Option?) {
            assertEquals(optionBackgroundColor, backgroundColor)
        }

        with(null as Multiselect.Option?) {
            assertEquals(stylesParent.multiselectOptionBackgroundColor, backgroundColor)
        }
    }

    @Test
    fun testOptionSelectedColorFallback() {
        val parent = object : BaseModel(), Styles {
            override var primaryColor = color(254, 0, 0, 0.5)
            override var multiselectOptionSelectedColor: PlatformColor? = null
        }

        // 40% lighter primary color w/ 100% alpha
        with(Multiselect.Option(Multiselect(parent))) {
            assertEquals(color(255, 203, 203, 1.0), selectedColor)
        }

        // 40% lighter of white primary color should still be white
        parent.primaryColor = TestColors.WHITE
        with(Multiselect.Option(Multiselect(parent))) {
            assertEquals(TestColors.WHITE, selectedColor)
        }

        parent.multiselectOptionSelectedColor = TestColors.RANDOM
        with(Multiselect.Option(Multiselect(parent))) {
            assertEquals(parent.multiselectOptionSelectedColor, selectedColor)
        }

        val multiselectSelectedColor = TestColors.RANDOM
        val multiselect = Multiselect(parent, optionSelectedColor = multiselectSelectedColor)
        with(Multiselect.Option(multiselect)) {
            assertEquals(multiselectSelectedColor, selectedColor)
        }

        val optionSelectedColor = TestColors.RANDOM
        with(Multiselect.Option(multiselect, selectedColor = optionSelectedColor)) {
            assertEquals(optionSelectedColor, selectedColor)
        }

        // test with nullable receiver
        with(Multiselect.Option(multiselect, selectedColor = optionSelectedColor) as Multiselect.Option?) {
            assertEquals(optionSelectedColor, selectedColor)
        }

        with(null as Multiselect.Option?) {
            assertEquals(stylesParent.defaultSelectedColor, selectedColor)
        }
    }

    private fun Multiselect.options(count: Int = 2) = List(count) { Multiselect.Option(this, value = "$it") }
}
