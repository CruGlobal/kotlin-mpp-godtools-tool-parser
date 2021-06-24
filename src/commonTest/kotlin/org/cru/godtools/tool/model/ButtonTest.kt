package org.cru.godtools.tool.model

import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.model.Button.Style.Companion.toButtonStyle
import org.cru.godtools.tool.model.Button.Type.Companion.toButtonTypeOrNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
class ButtonTest : UsesResources() {
    private val parent = object : BaseModel(), Styles {
        override lateinit var buttonStyle: Button.Style
        override var primaryColor = TestColors.BLACK
        override var primaryTextColor = TestColors.BLACK
    }

    @Test
    fun testParseButtonEvent() {
        val manifest = Manifest()
        val button = Button(manifest, getTestXmlParser("button_event.xml"))
        assertFalse(button.isIgnored)
        assertEquals(manifest.buttonStyle, button.style)
        assertEquals(Button.Type.EVENT, button.type)
        assertEquals(EventId.parse("event1 event2"), button.events)
        assertEquals("event button", button.text!!.text)
        assertEquals(TestColors.RED, button.buttonColor)
    }

    @Test
    fun testParseButtonUrl() {
        val button = Button(Manifest(), getTestXmlParser("button_url.xml"))
        assertFalse(button.isIgnored)
        assertEquals(Button.Style.OUTLINED, button.style)
        assertEquals(Button.Type.URL, button.type)
        assertEquals("https://www.google.com/", button.url!!.toString())
        assertEquals("url button", button.text!!.text)
        assertEquals(1, button.analyticsEvents.size)
        assertEquals("firebase action", button.analyticsEvents.single().action)
    }

    @Test
    fun testParseButtonRestrictTo() {
        val button = Button(Manifest(), getTestXmlParser("button_restrictTo.xml"))
        ParserConfig.supportedDeviceTypes = setOf(DeviceType.WEB)
        assertFalse(button.isIgnored)
        ParserConfig.supportedDeviceTypes = setOf(DeviceType.MOBILE)
        assertTrue(button.isIgnored)
    }

    @Test
    fun testButtonTypeUnknown() {
        val button = Button(Manifest(), type = Button.Type.UNKNOWN)
        assertTrue(button.isIgnored)
    }

    @Test
    fun testButtonStyleUnknown() {
        val button = Button(Manifest(), style = Button.Style.UNKNOWN)
        assertTrue(button.isIgnored)
    }

    @Test
    fun testButtonStyleUtilizesStylesDefault() {
        val button = Button(parent)
        parent.buttonStyle = Button.Style.CONTAINED
        assertEquals(Button.Style.CONTAINED, button.buttonStyle)
        parent.buttonStyle = Button.Style.OUTLINED
        assertEquals(Button.Style.OUTLINED, button.buttonStyle)
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
