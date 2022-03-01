package org.cru.godtools.tool.model

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.cru.godtools.tool.FEATURE_MULTISELECT
import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.model.AnalyticsEvent.Trigger
import org.cru.godtools.tool.model.Button.Style.Companion.toButtonStyle
import org.cru.godtools.tool.model.Button.Type.Companion.toButtonTypeOrNull
import org.cru.godtools.tool.state.State
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ButtonTest : UsesResources() {
    private val parent = object : BaseModel(), Styles {
        override lateinit var buttonStyle: Button.Style
        override var primaryColor = TestColors.BLACK
        override var primaryTextColor = TestColors.BLACK
        override var textAlign = Text.Align.START
    }
    private val state = State()

    // region Parse Button
    @Test
    fun testParseButtonEvent() = runTest {
        val manifest = Manifest()
        with(Button(manifest, getTestXmlParser("button_event.xml"))) {
            assertFalse(testIsIgnored)
            assertFalse(isGone(state))
            assertFalse(isInvisible(state))
            assertEquals(manifest.buttonStyle, style)
            assertEquals(Button.Type.EVENT, type)
            assertEquals(EventId.parse("event1 event2"), events)
            assertEquals("event button", text.text)
            assertEquals(TestColors.RED, buttonColor)
            assertEquals(Button.DEFAULT_BACKGROUND_COLOR, backgroundColor)
            assertEquals(Dimension.Pixels(50), width)
            assertEquals(Gravity.Horizontal.END, gravity)

            assertNull(icon)
            assertEquals(Gravity.Horizontal.START, iconGravity)
            assertEquals(Button.DEFAULT_ICON_SIZE, iconSize)
        }
    }

    @Test
    fun testParseButtonUrl() = runTest {
        val button = Button(Manifest(), getTestXmlParser("button_url.xml"))
        assertFalse(button.testIsIgnored)
        assertEquals(Button.Style.OUTLINED, button.style)
        assertEquals(Button.Type.URL, button.type)
        assertEquals(TestColors.GREEN, button.backgroundColor)
        assertEquals("https://www.google.com/", button.url!!.toString())
        assertEquals("url button", button.text.text)
        assertEquals(1, button.analyticsEvents.size)
        assertEquals("firebase action", button.analyticsEvents.single().action)
        assertEquals(Button.DEFAULT_WIDTH, button.width)
        assertEquals(Button.DEFAULT_GRAVITY, button.gravity)
    }

    @Test
    fun testParseButtonRestrictTo() = runTest {
        val button = Button(Manifest(), getTestXmlParser("button_restrictTo.xml"))
        ParserConfig.supportedDeviceTypes = setOf(DeviceType.WEB)
        assertFalse(button.testIsIgnored)
        ParserConfig.supportedDeviceTypes = setOf(DeviceType.MOBILE)
        assertTrue(button.testIsIgnored)
    }

    @Test
    fun testParseButtonRequiredFeatures() = runTest {
        val button = Button(Manifest(), getTestXmlParser("button_requiredFeatures.xml"))
        ParserConfig.supportedFeatures = setOf(FEATURE_MULTISELECT)
        assertFalse(button.testIsIgnored)
        ParserConfig.supportedFeatures = emptySet()
        assertTrue(button.testIsIgnored)
    }

    @Test
    fun testParseButtonIcon() = runTest {
        val resource = Resource(name = "image.png")
        with(Button(Manifest(resources = { listOf(resource) }), getTestXmlParser("button_icon.xml"))) {
            assertSame(resource, icon)
            assertEquals(Gravity.Horizontal.END, iconGravity)
            assertEquals(24, iconSize)
        }
    }

    @Test
    fun testParseButtonVisibility() = runTest {
        with(Button(Manifest(), getTestXmlParser("button_visibility.xml"))) {
            assertFalse(testIsIgnored)

            assertFalse(isInvisible(state))
            state["invisible"] = "true"
            assertTrue(isInvisible(state))

            assertFalse(isGone(state))
            state["hidden"] = "true"
            assertTrue(isGone(state))
        }
    }
    // endregion Parse Button

    // region Property - isIgnored
    @Test
    fun testIsIgnoredClickable() {
        with(Button()) {
            assertFalse(isClickable)
            assertTrue(testIsIgnored)
        }

        with(Button(events = listOf(EventId.FOLLOWUP))) {
            assertTrue(isClickable)
            assertFalse(testIsIgnored)
        }

        with(Button(url = TEST_URL)) {
            assertTrue(isClickable)
            assertFalse(testIsIgnored)
        }
    }

    @Test
    fun testButtonStyleUnknown() {
        val button = Button(style = Button.Style.UNKNOWN, events = listOf(EventId.FOLLOWUP))
        assertTrue(button.testIsIgnored)
    }
    // endregion Property - isIgnored

    // region Property - style
    @Test
    fun testButtonStyleUtilizesStylesDefault() {
        with(Button(parent)) {
            parent.buttonStyle = Button.Style.CONTAINED
            assertEquals(Button.Style.CONTAINED, style)
            parent.buttonStyle = Button.Style.OUTLINED
            assertEquals(Button.Style.OUTLINED, style)
        }

        parent.buttonStyle = Button.Style.CONTAINED
        with(Button(parent, style = Button.Style.OUTLINED)) {
            assertNotEquals(parent.buttonStyle, style)
            assertEquals(Button.Style.OUTLINED, style)
        }
    }
    // endregion Property - style

    // region Property - buttonColor
    @Test
    fun testButtonColorFallbackBehavior() {
        val manifest = Manifest()
        assertEquals(manifest.primaryColor, Button(manifest).buttonColor)
        with(Button(manifest, color = TestColors.GREEN)) {
            assertEquals(TestColors.GREEN, buttonColor)
            assertNotEquals(manifest.primaryColor, buttonColor)
        }

        val parent = object : BaseModel(manifest), Styles {
            override val primaryColor = TestColors.BLUE
        }
        with(Button(parent)) {
            assertEquals(parent.primaryColor, buttonColor)
            assertNotEquals(manifest.primaryColor, buttonColor)
        }
        with(Button(parent, color = TestColors.GREEN)) {
            assertEquals(TestColors.GREEN, buttonColor)
            assertNotEquals(parent.primaryColor, buttonColor)
            assertNotEquals(manifest.primaryColor, buttonColor)
        }
    }
    // endregion Property - buttonColor

    // region Property - text - textAlign
    @Test
    fun testButtonTextTextAlignFallbackBehavior() {
        parent.textAlign = Text.Align.START

        // Buttons default to center aligned text
        with(Button(parent, text = { Text(it) })) {
            assertNotEquals(parent.textAlign, text.textAlign)
            assertEquals(Text.Align.CENTER, text.textAlign)
        }

        // Text Align can still be overridden on the text element
        with(Button(parent, text = { Text(it, textAlign = Text.Align.END) })) {
            assertNotEquals(parent.textAlign, text.textAlign)
            assertEquals(Text.Align.END, text.textAlign)
        }
    }
    // endregion Property - text - textAlign

    // region Property - text - textColor
    @Test
    fun testButtonTextColorFallbackBehaviorContained() {
        parent.primaryColor = TestColors.RED
        parent.primaryTextColor = TestColors.GREEN

        with(Button(parent, style = Button.Style.CONTAINED, text = { Text(it) })) {
            assertNotEquals(parent.primaryColor, text.textColor)
            assertNotEquals(parent.textColor, text.textColor)
            assertNotEquals(buttonColor, text.textColor)
            assertEquals(parent.primaryTextColor, text.textColor)
            assertEquals(TestColors.GREEN, text.textColor)
        }

        with(Button(parent, style = Button.Style.CONTAINED, text = { Text(it, textColor = TestColors.BLUE) })) {
            assertNotEquals(parent.primaryTextColor, text.textColor)
            assertEquals(TestColors.BLUE, text.textColor)
        }
    }

    @Test
    fun testButtonTextColorFallbackBehaviorOutlined() {
        parent.primaryColor = TestColors.RED
        parent.primaryTextColor = TestColors.RED

        with(Button(parent, style = Button.Style.OUTLINED, color = TestColors.GREEN, text = { Text(it) })) {
            assertNotEquals(parent.primaryColor, text.textColor)
            assertNotEquals(parent.primaryTextColor, text.textColor)
            assertNotEquals(parent.textColor, text.textColor)
            assertEquals(buttonColor, text.textColor)
            assertEquals(TestColors.GREEN, text.textColor)
        }

        with(
            Button(
                parent,
                style = Button.Style.OUTLINED,
                color = TestColors.RED,
                text = { Text(it, textColor = TestColors.GREEN) }
            )
        ) {
            assertNotEquals(buttonColor, text.textColor)
            assertEquals(TestColors.GREEN, text.textColor)
        }
    }
    // endregion Property - text - textColor

    @Test
    fun testButtonGetAnalyticsEvents() {
        val defaultEvent = AnalyticsEvent(trigger = Trigger.DEFAULT)
        val clickedEvent = AnalyticsEvent(trigger = Trigger.CLICKED)
        val selectedEvent = AnalyticsEvent(trigger = Trigger.SELECTED)
        val visibleEvent = AnalyticsEvent(trigger = Trigger.VISIBLE)
        val button = Button(analyticsEvents = listOf(defaultEvent, clickedEvent, selectedEvent, visibleEvent))

        assertEquals(listOf(defaultEvent, clickedEvent, selectedEvent), button.getAnalyticsEvents(Trigger.CLICKED))
        assertFailsWith(IllegalStateException::class) { button.getAnalyticsEvents(Trigger.DEFAULT) }
        assertFailsWith(IllegalStateException::class) { button.getAnalyticsEvents(Trigger.SELECTED) }
        assertFailsWith(IllegalStateException::class) { button.getAnalyticsEvents(Trigger.VISIBLE) }
    }

    // region Button.Style
    @Test
    fun verifyParseButtonStyle() {
        assertEquals(Button.Style.CONTAINED, "contained".toButtonStyle())
        assertEquals(Button.Style.OUTLINED, "outlined".toButtonStyle())
        assertEquals(Button.Style.UNKNOWN, "ahjkwer".toButtonStyle())
    }
    // endregion Button.Style

    // region Button.Type
    @Test
    fun verifyParseButtonType() {
        assertEquals(Button.Type.URL, "url".toButtonTypeOrNull())
        assertEquals(Button.Type.EVENT, "event".toButtonTypeOrNull())
        assertNull("ahjkwer".toButtonTypeOrNull())
    }
    // endregion Button.Type
}
