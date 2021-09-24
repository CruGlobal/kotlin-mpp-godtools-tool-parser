package org.cru.godtools.tool.model

import org.cru.godtools.tool.FEATURE_MULTISELECT
import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.internal.runBlockingTest
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
class ButtonTest : UsesResources() {
    private val parent = object : BaseModel(), Styles {
        override lateinit var buttonStyle: Button.Style
        override var primaryColor = TestColors.BLACK
        override var primaryTextColor = TestColors.BLACK
    }
    private val state = State()

    @Test
    fun testParseButtonEvent() = runBlockingTest {
        val manifest = Manifest()
        with(Button(manifest, getTestXmlParser("button_event.xml"))) {
            assertFalse(testIsIgnored)
            assertFalse(isGone(state))
            assertFalse(isInvisible(state))
            assertEquals(manifest.buttonStyle, style)
            assertEquals(Button.Type.EVENT, type)
            assertEquals(EventId.parse("event1 event2"), events)
            assertEquals("event button", text!!.text)
            assertEquals(TestColors.RED, buttonColor)
            assertEquals(Button.DEFAULT_BACKGROUND_COLOR, backgroundColor)

            assertNull(icon)
            assertTrue(iconGravity.isStart)
            assertEquals(Button.DEFAULT_ICON_SIZE, iconSize)
        }
    }

    @Test
    fun testParseButtonUrl() = runBlockingTest {
        val button = Button(Manifest(), getTestXmlParser("button_url.xml"))
        assertFalse(button.testIsIgnored)
        assertEquals(Button.Style.OUTLINED, button.style)
        assertEquals(Button.Type.URL, button.type)
        assertEquals(TestColors.GREEN, button.backgroundColor)
        assertEquals("https://www.google.com/", button.url!!.toString())
        assertEquals("url button", button.text!!.text)
        assertEquals(1, button.analyticsEvents.size)
        assertEquals("firebase action", button.analyticsEvents.single().action)
    }

    @Test
    fun testParseButtonRestrictTo() = runBlockingTest {
        val button = Button(Manifest(), getTestXmlParser("button_restrictTo.xml"))
        ParserConfig.supportedDeviceTypes = setOf(DeviceType.WEB)
        assertFalse(button.testIsIgnored)
        ParserConfig.supportedDeviceTypes = setOf(DeviceType.MOBILE)
        assertTrue(button.testIsIgnored)
    }

    @Test
    fun testParseButtonRequiredFeatures() = runBlockingTest {
        val button = Button(Manifest(), getTestXmlParser("button_requiredFeatures.xml"))
        ParserConfig.supportedFeatures = setOf(FEATURE_MULTISELECT)
        assertFalse(button.testIsIgnored)
        ParserConfig.supportedFeatures = emptySet()
        assertTrue(button.testIsIgnored)
    }

    @Test
    fun testParseButtonIcon() = runBlockingTest {
        val resource = Resource(name = "image.png")
        with(Button(Manifest(resources = { listOf(resource) }), getTestXmlParser("button_icon.xml"))) {
            assertSame(resource, icon)
            assertTrue(iconGravity.isEnd)
            assertEquals(24, iconSize)
        }
    }

    @Test
    fun testParseButtonVisibility() = runBlockingTest {
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

    // region isIgnored
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
    // endregion isIgnored

    @Test
    fun testButtonStyleUtilizesStylesDefault() {
        val button = Button(parent)
        parent.buttonStyle = Button.Style.CONTAINED
        assertEquals(Button.Style.CONTAINED, button.buttonStyle)
        parent.buttonStyle = Button.Style.OUTLINED
        assertEquals(Button.Style.OUTLINED, button.buttonStyle)
    }

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

    @Test
    fun testButtonTextColorFallbackBehaviorContained() {
        parent.primaryColor = TestColors.RED
        parent.primaryTextColor = TestColors.GREEN

        with(Button(parent, style = Button.Style.CONTAINED, text = { Text(it) })) {
            assertNotEquals(parent.primaryColor, text!!.textColor)
            assertNotEquals(parent.textColor, text!!.textColor)
            assertEquals(primaryTextColor, text!!.textColor)
            assertEquals(TestColors.GREEN, text!!.textColor)
        }

        with(Button(parent, style = Button.Style.CONTAINED, text = { Text(it, textColor = TestColors.BLUE) })) {
            assertNotEquals(primaryTextColor, text!!.textColor)
            assertNotEquals(textColor, text!!.textColor)
            assertEquals(TestColors.BLUE, text!!.textColor)
        }
    }

    @Test
    fun testButtonTextColorFallbackBehaviorOutlined() {
        parent.primaryColor = TestColors.RED
        parent.primaryTextColor = TestColors.RED

        with(Button(parent, style = Button.Style.OUTLINED, color = TestColors.GREEN, text = { Text(it) })) {
            assertNotEquals(parent.primaryColor, text!!.textColor)
            assertNotEquals(parent.primaryTextColor, text!!.textColor)
            assertNotEquals(parent.textColor, text!!.textColor)
            assertEquals(buttonColor, text!!.textColor)
            assertEquals(TestColors.GREEN, text!!.textColor)
        }

        with(
            Button(
                parent,
                style = Button.Style.OUTLINED,
                color = TestColors.RED,
                text = { Text(it, textColor = TestColors.GREEN) }
            )
        ) {
            assertNotEquals(buttonColor, text!!.textColor)
            assertNotEquals(textColor, text!!.textColor)
            assertEquals(TestColors.GREEN, text!!.textColor)
        }
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
