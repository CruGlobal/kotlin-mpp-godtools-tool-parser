package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.ParserConfig
import org.cru.godtools.shared.tool.parser.ParserConfig.Companion.FEATURE_MULTISELECT
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Trigger
import org.cru.godtools.shared.tool.parser.model.Button.Style.Companion.toButtonStyle
import org.cru.godtools.shared.tool.parser.model.Button.Type.Companion.toButtonTypeOrNull
import org.cru.godtools.shared.tool.state.State

@RunOnAndroidWith(AndroidJUnit4::class)
class ButtonTest : UsesResources() {
    private val parentObj = object : BaseModel(), Styles {
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
            assertFalse(isIgnored)
            assertFalse(isGone(state))
            assertFalse(isInvisible(state))
            assertEquals(manifest.buttonStyle, style)
            assertEquals(listOf(EventId(name = "event1"), EventId(name = "event2")), events)
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
        assertFalse(button.isIgnored)
        assertEquals(Button.Style.OUTLINED, button.style)
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
        val webConfig = ParserConfig().withAppVersion(DeviceType.WEB, null)
        with(Button(Manifest(config = webConfig), getTestXmlParser("button_restrictTo.xml"))) {
            assertFalse(isIgnored)
        }

        val androidConfig = ParserConfig().withAppVersion(DeviceType.ANDROID, null)
        with(Button(Manifest(config = androidConfig), getTestXmlParser("button_restrictTo.xml"))) {
            assertTrue(isIgnored)
        }

        val iosConfig = ParserConfig().withAppVersion(DeviceType.IOS, null)
        with(Button(Manifest(config = iosConfig), getTestXmlParser("button_restrictTo.xml"))) {
            assertTrue(isIgnored)
        }
    }

    @Test
    fun testParseButtonRequiredFeatures() = runTest {
        with(Button(Manifest(), getTestXmlParser("button_requiredFeatures.xml"))) {
            assertTrue(isIgnored)
        }

        val config = ParserConfig().withSupportedFeatures(FEATURE_MULTISELECT)
        with(Button(Manifest(config = config), getTestXmlParser("button_requiredFeatures.xml"))) {
            assertFalse(isIgnored)
        }
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
            assertFalse(isIgnored)

            assertFalse(isInvisible(state))
            state.setVar("invisible", listOf("true"))
            assertTrue(isInvisible(state))

            assertFalse(isGone(state))
            state.setVar("hidden", listOf("true"))
            assertTrue(isGone(state))
        }
    }
    // endregion Parse Button

    // region Property - isIgnored
    @Test
    fun testIsIgnoredClickable() {
        with(Button()) {
            assertFalse(isClickable)
            assertTrue(isIgnored)
        }

        with(Button(events = listOf(EventId.FOLLOWUP))) {
            assertTrue(isClickable)
            assertFalse(isIgnored)
        }

        with(Button(url = TEST_URL)) {
            assertTrue(isClickable)
            assertFalse(isIgnored)
        }
    }

    @Test
    fun testButtonStyleUnknown() {
        val button = Button(style = Button.Style.UNKNOWN, events = listOf(EventId.FOLLOWUP))
        assertTrue(button.isIgnored)
    }
    // endregion Property - isIgnored

    // region Property - style
    @Test
    fun testButtonStyleUtilizesStylesDefault() {
        with(Button(parentObj)) {
            parentObj.buttonStyle = Button.Style.CONTAINED
            assertEquals(Button.Style.CONTAINED, style)
            parentObj.buttonStyle = Button.Style.OUTLINED
            assertEquals(Button.Style.OUTLINED, style)
        }

        parentObj.buttonStyle = Button.Style.CONTAINED
        with(Button(parentObj, style = Button.Style.OUTLINED)) {
            assertNotEquals(parentObj.buttonStyle, style)
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
            override val primaryColor = TestColors.random()
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
        parentObj.textAlign = Text.Align.START

        // Buttons default to center aligned text
        with(Button(parentObj, text = { Text(it) })) {
            assertNotEquals(parentObj.textAlign, text.textAlign)
            assertEquals(Text.Align.CENTER, text.textAlign)
        }

        // Text Align can still be overridden on the text element
        with(Button(parentObj, text = { Text(it, textAlign = Text.Align.END) })) {
            assertNotEquals(parentObj.textAlign, text.textAlign)
            assertEquals(Text.Align.END, text.textAlign)
        }
    }
    // endregion Property - text - textAlign

    // region Property - text - textColor
    @Test
    fun testButtonTextColorFallbackBehaviorContained() {
        parentObj.primaryColor = TestColors.RED
        parentObj.primaryTextColor = TestColors.GREEN

        with(Button(parentObj, style = Button.Style.CONTAINED, text = { Text(it) })) {
            assertNotEquals(parentObj.primaryColor, text.textColor)
            assertNotEquals(parentObj.textColor, text.textColor)
            assertNotEquals(buttonColor, text.textColor)
            assertEquals(parentObj.primaryTextColor, text.textColor)
            assertEquals(TestColors.GREEN, text.textColor)
        }

        with(Button(parentObj, style = Button.Style.CONTAINED, text = { Text(it, textColor = TestColors.BLUE) })) {
            assertNotEquals(parentObj.primaryTextColor, text.textColor)
            assertEquals(TestColors.BLUE, text.textColor)
        }
    }

    @Test
    fun testButtonTextColorFallbackBehaviorOutlined() {
        parentObj.primaryColor = TestColors.RED
        parentObj.primaryTextColor = TestColors.RED

        with(Button(parentObj, style = Button.Style.OUTLINED, color = TestColors.GREEN, text = { Text(it) })) {
            assertNotEquals(parentObj.primaryColor, text.textColor)
            assertNotEquals(parentObj.primaryTextColor, text.textColor)
            assertNotEquals(parentObj.textColor, text.textColor)
            assertEquals(buttonColor, text.textColor)
            assertEquals(TestColors.GREEN, text.textColor)
        }

        with(
            Button(
                parentObj,
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
        val visibleEvent = AnalyticsEvent(trigger = Trigger.VISIBLE)
        val button = Button(analyticsEvents = listOf(defaultEvent, clickedEvent, visibleEvent))

        assertEquals(listOf(defaultEvent, clickedEvent), button.getAnalyticsEvents(Trigger.CLICKED))
        assertFailsWith(IllegalStateException::class) { button.getAnalyticsEvents(Trigger.DEFAULT) }
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
