package org.cru.godtools.tool.model

import org.cru.godtools.tool.DEFAULT_SUPPORTED_DEVICE_TYPES
import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.model.Content.Companion.parseContentElement
import org.cru.godtools.tool.model.tips.InlineTip
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
class ContentTest : UsesResources() {
    @BeforeTest
    fun setupConfig() {
        ParserConfig.supportedDeviceTypes = setOf(DeviceType.ANDROID)
    }

    @AfterTest
    fun resetConfig() {
        ParserConfig.supportedDeviceTypes = DEFAULT_SUPPORTED_DEVICE_TYPES
    }

    @Test
    fun verifyRestrictToSupported() {
        assertFalse(object : Content(Manifest(), restrictTo = DeviceType.ALL) {}.isIgnored)
        assertFalse(object : Content(Manifest(), restrictTo = DeviceType.SUPPORTED) {}.isIgnored)
        assertFalse(object : Content(Manifest(), restrictTo = setOf(DeviceType.ANDROID)) {}.isIgnored)
    }

    @Test
    fun verifyRestrictToNotSupported() {
        assertTrue(object : Content(Manifest(), restrictTo = setOf(DeviceType.UNKNOWN)) {}.isIgnored)
        assertTrue(object : Content(Manifest(), restrictTo = setOf(DeviceType.IOS)) {}.isIgnored)
    }

    @Test
    fun verifyVersionSupported() {
        assertFalse(object : Content(Manifest(), version = SCHEMA_VERSION) {}.isIgnored)
    }

    @Test
    fun verifyVersionNotSupported() {
        assertTrue(object : Content(Manifest(), version = SCHEMA_VERSION + 1) {}.isIgnored)
    }

    // region parseContentElement()
    @Test
    fun verifyParseContentElementAccordion() {
        assertIs<Accordion>(getTestXmlParser("accordion.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementAnimation() {
        assertIs<Animation>(getTestXmlParser("animation.xml").parseContentElement(Manifest()))
        assertIs<Animation>(getTestXmlParser("animation_defaults.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementButton() {
        assertIs<Button>(getTestXmlParser("button_event.xml").parseContentElement(Manifest()))
        assertIs<Button>(getTestXmlParser("button_restrictTo.xml").parseContentElement(Manifest()))
        assertIs<Button>(getTestXmlParser("button_url.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementFallback() {
        assertIs<Fallback>(getTestXmlParser("fallback.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementForm() {
        assertIs<Form>(getTestXmlParser("form.xml").parseContentElement(Manifest()))
        assertIs<Form>(getTestXmlParser("form_ignored_content.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementImage() {
        assertIs<Image>(getTestXmlParser("image.xml").parseContentElement(Manifest()))
        assertIs<Image>(getTestXmlParser("image_restricted.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementInlineTip() {
        assertIs<InlineTip>(getTestXmlParser("tips/inline_tip.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementInput() {
        assertIs<Input>(getTestXmlParser("input_email.xml").parseContentElement(Manifest()))
        assertIs<Input>(getTestXmlParser("input_hidden.xml").parseContentElement(Manifest()))
        assertIs<Input>(getTestXmlParser("input_text.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementParagraph() {
        assertIs<Paragraph>(getTestXmlParser("paragraph.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementParagraphFallback() {
        assertIs<Fallback>(getTestXmlParser("fallback_paragraph.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementText() {
        assertIs<Text>(getTestXmlParser("text_attributes.xml").parseContentElement(Manifest()))
        assertIs<Text>(getTestXmlParser("text_defaults.xml").parseContentElement(Manifest()))
    }
//
//    @Test
//    fun testFromXmlSpacer() {
//        val content = Content.fromXml(Manifest(), getXmlParserForResource("spacer.xml"), true)
//        assertTrue(content is Spacer)
//    }

    @Test
    fun verifyParseContentElementUnrecognized() {
        assertNull(getTestXmlParser("content_unrecognized.xml").parseContentElement(Manifest()))
    }
    // endregion parseContentElement()
}
