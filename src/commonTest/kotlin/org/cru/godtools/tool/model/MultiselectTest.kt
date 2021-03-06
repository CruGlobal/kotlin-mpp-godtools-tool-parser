package org.cru.godtools.tool.model

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.cru.godtools.tool.FEATURE_MULTISELECT
import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.internal.receive
import org.cru.godtools.tool.internal.runBlockingTest
import org.cru.godtools.tool.state.State
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
class MultiselectTest : UsesResources() {
    private val state by lazy { State() }

    @Test
    fun testParseMultiselect() = runBlockingTest {
        val multiselect = Multiselect(Manifest(), getTestXmlParser("multiselect.xml"))
        assertEquals("quiz1", multiselect.stateName)
        assertEquals(2, multiselect.selectionLimit)
        assertEquals(3, multiselect.options.size)
        with(multiselect.options[0]) {
            assertEquals(TestColors.RED, backgroundColor)
            assertEquals(TestColors.BLUE, selectedColor)
        }
        with(multiselect.options[1]) {
            assertEquals("answer2", value)
            assertEquals(TestColors.BLUE, backgroundColor)
            assertEquals(TestColors.GREEN, selectedColor)
            assertEquals(1, content.size)
            with(assertIs<Text>(content.single())) {
                assertEquals("Answer 2", text)
            }
        }
    }

    @Test
    fun testParseMultiselectDefaults() = runBlockingTest {
        val manifest = Manifest(multiselectOptionSelectedColor = TestColors.RANDOM)
        val multiselect = Multiselect(manifest, getTestXmlParser("multiselect_defaults.xml"))
        assertEquals("", multiselect.stateName)
        assertEquals(1, multiselect.selectionLimit)
        assertEquals(1, multiselect.options.size)
        with(multiselect.options.single()) {
            assertEquals("valueAttr", value)
            assertEquals(manifest.backgroundColor, backgroundColor)
            assertEquals(manifest.multiselectOptionSelectedColor, selectedColor)
            assertTrue(content.isEmpty())
        }
    }

    @Test
    fun testIsIgnored() {
        val multiselect = Multiselect()

        ParserConfig.supportedFeatures = setOf(FEATURE_MULTISELECT)
        assertFalse(multiselect.isIgnored)

        ParserConfig.supportedFeatures = emptySet()
        assertTrue(multiselect.isIgnored)
    }

    @Test
    fun testOptionIsSelected() {
        val multiselect = Multiselect { it.options(2) }

        multiselect.options[0].toggleSelected(state)
        assertTrue(multiselect.options[0].isSelected(state))
        assertFalse(multiselect.options[1].isSelected(state))
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun testOptionIsSelectedFlow() = runBlockingTest {
        val multiselect = Multiselect { it.options(2) }

        val flowOutput = Channel<Boolean>(1)
        val flow = multiselect.options[0].isSelectedFlow(state)
            .onEach { flowOutput.send(it) }
            .launchIn(this)
        assertFalse("Initially not selected") { flowOutput.receive(100) }

        multiselect.options[0].toggleSelected(state)
        println("Toggled this option to true")
        assertTrue(flowOutput.receive(100))

        multiselect.options[1].toggleSelected(state)
        assertFalse(flowOutput.receive(100), "Toggled other option to true")

        multiselect.options[1].toggleSelected(state)
        delay(100)
        assertTrue(flowOutput.isEmpty, "Toggled other option to false")

        state["other"] = "test"
        delay(100)
        assertTrue(flowOutput.isEmpty, "Set different state value")

        flow.cancel()
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
            override val primaryColor = color(255, 0, 0, 0.5)
            override var multiselectOptionSelectedColor: PlatformColor? = null
        }

        // 40% lighter primary color w/ 100% alpha
        with(Multiselect.Option(Multiselect(parent))) {
            assertEquals(color(255, 204, 204, 1.0), selectedColor)
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
