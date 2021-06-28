package org.cru.godtools.tool.model

import org.cru.godtools.tool.DEFAULT_SUPPORTED_DEVICE_TYPES
import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.internal.runBlockingTest
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
    fun verifyParseContentElementAccordion() = runBlockingTest {
        assertIs<Accordion>(getTestXmlParser("accordion.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementAnimation() = runBlockingTest {
        assertIs<Animation>(getTestXmlParser("animation.xml").parseContentElement(Manifest()))
        assertIs<Animation>(getTestXmlParser("animation_defaults.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementButton() = runBlockingTest {
        assertIs<Button>(getTestXmlParser("button_event.xml").parseContentElement(Manifest()))
        assertIs<Button>(getTestXmlParser("button_restrictTo.xml").parseContentElement(Manifest()))
        assertIs<Button>(getTestXmlParser("button_url.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementFallback() = runBlockingTest {
        assertIs<Fallback>(getTestXmlParser("fallback.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementForm() = runBlockingTest {
        assertIs<Form>(getTestXmlParser("form.xml").parseContentElement(Manifest()))
        assertIs<Form>(getTestXmlParser("form_ignored_content.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementImage() = runBlockingTest {
        assertIs<Image>(getTestXmlParser("image.xml").parseContentElement(Manifest()))
        assertIs<Image>(getTestXmlParser("image_restricted.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementInlineTip() = runBlockingTest {
        assertIs<InlineTip>(getTestXmlParser("tips/inline_tip.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementInput() = runBlockingTest {
        assertIs<Input>(getTestXmlParser("input_email.xml").parseContentElement(Manifest()))
        assertIs<Input>(getTestXmlParser("input_hidden.xml").parseContentElement(Manifest()))
        assertIs<Input>(getTestXmlParser("input_text.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementLink() = runBlockingTest {
        assertIs<Link>(getTestXmlParser("link.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementParagraph() = runBlockingTest {
        assertIs<Paragraph>(getTestXmlParser("paragraph.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementParagraphFallback() = runBlockingTest {
        assertIs<Fallback>(getTestXmlParser("fallback_paragraph.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementSpacer() = runBlockingTest {
        assertIs<Spacer>(getTestXmlParser("spacer.xml").parseContentElement(Manifest()))
        assertIs<Spacer>(getTestXmlParser("spacer_fixed.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementTabs() = runBlockingTest {
        assertIs<Tabs>(getTestXmlParser("tabs_empty.xml").parseContentElement(Manifest()))
        assertIs<Tabs>(getTestXmlParser("tabs_single.xml").parseContentElement(Manifest()))
        assertIs<Tabs>(getTestXmlParser("tabs_multiple.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementText() = runBlockingTest {
        assertIs<Text>(getTestXmlParser("text_attributes.xml").parseContentElement(Manifest()))
        assertIs<Text>(getTestXmlParser("text_defaults.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementVideo() = runBlockingTest {
        assertIs<Video>(getTestXmlParser("video.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementUnrecognized() = runBlockingTest {
        assertNull(getTestXmlParser("content_unrecognized.xml").parseContentElement(Manifest()))
    }
    // endregion parseContentElement()
}
