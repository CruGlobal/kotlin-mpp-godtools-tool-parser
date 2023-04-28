package org.cru.godtools.shared.tool.parser.model

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.ParserConfig
import org.cru.godtools.shared.tool.parser.ParserConfig.Companion.FEATURE_ANIMATION
import org.cru.godtools.shared.tool.parser.ParserConfig.Companion.FEATURE_MULTISELECT
import org.cru.godtools.shared.tool.parser.expressions.toExpressionOrNull
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.Content.Companion.parseContentElement
import org.cru.godtools.shared.tool.parser.model.Version.Companion.toVersion
import org.cru.godtools.shared.tool.parser.model.tips.InlineTip
import org.cru.godtools.shared.tool.parser.withDeviceType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ContentTest : UsesResources() {
    // region required-features
    @Test
    fun verifyRequiredFeaturesSupported() {
        val manifest = Manifest(ParserConfig().withSupportedFeatures(FEATURE_ANIMATION, FEATURE_MULTISELECT))
        assertFalse(
            object : Content(manifest, requiredFeatures = setOf(FEATURE_ANIMATION, FEATURE_MULTISELECT)) {}.isIgnored
        )
        assertFalse(object : Content(manifest, requiredFeatures = setOf(FEATURE_ANIMATION)) {}.isIgnored)
        assertFalse(object : Content(manifest, requiredFeatures = setOf(FEATURE_MULTISELECT)) {}.isIgnored)
        assertFalse(object : Content(manifest, requiredFeatures = emptySet()) {}.isIgnored)
    }

    @Test
    fun verifyRequiredFeaturesNotSupported() {
        val manifest = Manifest(ParserConfig().withSupportedFeatures(FEATURE_ANIMATION))
        assertTrue(
            object : Content(manifest, requiredFeatures = setOf(FEATURE_ANIMATION, FEATURE_MULTISELECT)) {}.isIgnored
        )
        assertTrue(object : Content(manifest, requiredFeatures = setOf(FEATURE_MULTISELECT)) {}.isIgnored)
        assertTrue(object : Content(manifest, requiredFeatures = setOf("kjlasdf")) {}.isIgnored)
    }
    // endregion required-features

    // region required-device-type
    @Test
    fun verifyRequiredDeviceType() {
        val android = Manifest(ParserConfig().withDeviceType(DeviceType.ANDROID))
        assertFalse(object : Content(android, requiredDeviceType = DeviceType.ALL) {}.isIgnored)
        assertFalse(object : Content(android, requiredDeviceType = setOf(DeviceType.ANDROID)) {}.isIgnored)
        assertFalse(object : Content(android, requiredDeviceType = setOf(DeviceType.MOBILE)) {}.isIgnored)
        assertTrue(object : Content(android, requiredDeviceType = setOf(DeviceType.IOS)) {}.isIgnored)
        assertTrue(object : Content(android, requiredDeviceType = setOf(DeviceType.WEB)) {}.isIgnored)
        assertTrue(object : Content(android, requiredDeviceType = setOf(DeviceType.UNKNOWN)) {}.isIgnored)

        val ios = Manifest(ParserConfig().withDeviceType(DeviceType.IOS))
        assertFalse(object : Content(ios, requiredDeviceType = DeviceType.ALL) {}.isIgnored)
        assertFalse(object : Content(ios, requiredDeviceType = setOf(DeviceType.IOS)) {}.isIgnored)
        assertFalse(object : Content(ios, requiredDeviceType = setOf(DeviceType.MOBILE)) {}.isIgnored)
        assertTrue(object : Content(ios, requiredDeviceType = setOf(DeviceType.ANDROID)) {}.isIgnored)
        assertTrue(object : Content(ios, requiredDeviceType = setOf(DeviceType.WEB)) {}.isIgnored)
        assertTrue(object : Content(ios, requiredDeviceType = setOf(DeviceType.UNKNOWN)) {}.isIgnored)

        val web = Manifest(ParserConfig().withDeviceType(DeviceType.WEB))
        assertFalse(object : Content(web, requiredDeviceType = DeviceType.ALL) {}.isIgnored)
        assertFalse(object : Content(web, requiredDeviceType = setOf(DeviceType.WEB)) {}.isIgnored)
        assertTrue(object : Content(web, requiredDeviceType = setOf(DeviceType.ANDROID)) {}.isIgnored)
        assertTrue(object : Content(web, requiredDeviceType = setOf(DeviceType.IOS)) {}.isIgnored)
        assertTrue(object : Content(web, requiredDeviceType = setOf(DeviceType.MOBILE)) {}.isIgnored)
        assertTrue(object : Content(web, requiredDeviceType = setOf(DeviceType.UNKNOWN)) {}.isIgnored)
    }
    // endregion required-device-type

    // region version
    @Test
    fun verifyVersionSupported() {
        assertFalse(object : Content(Manifest(), version = SCHEMA_VERSION) {}.isIgnored)
    }

    @Test
    fun verifyVersionNotSupported() {
        assertTrue(object : Content(Manifest(), version = SCHEMA_VERSION + 1) {}.isIgnored)
    }
    // endregion version

    // region required-versions
    @Test
    fun verifyRequiredAndroidVersion() {
        // requiredAndroidVersion is not satisfied if the device version hasn't been configured
        val default = Manifest(ParserConfig())
        assertTrue(object : Content(default, requiredAndroidVersion = "2".toVersion()) {}.isIgnored)

        // requireAndroidVersion is satisfied for any newer Android app version
        val android = Manifest(config = ParserConfig().withAppVersion(DeviceType.ANDROID, "2"))
        assertFalse(object : Content(android) {}.isIgnored)
        assertFalse(object : Content(android, requiredAndroidVersion = "1".toVersion()) {}.isIgnored)
        assertFalse(object : Content(android, requiredAndroidVersion = "2".toVersion()) {}.isIgnored)
        assertTrue(object : Content(android, requiredAndroidVersion = "2.0.0.1".toVersion()) {}.isIgnored)
        assertTrue(object : Content(android, requiredAndroidVersion = "3".toVersion()) {}.isIgnored)

        // requiredAndroidVersion is satisfied for any ios app version
        val ios = Manifest(config = ParserConfig().withAppVersion(DeviceType.IOS, "2"))
        assertFalse(object : Content(ios, requiredAndroidVersion = Version.MAX) {}.isIgnored)
    }

    @Test
    fun verifyRequiredIosVersion() {
        // requiredIosVersion is not satisfied if the device version hasn't been configured
        val default = Manifest(ParserConfig())
        assertTrue(object : Content(default, requiredIosVersion = "2".toVersion()) {}.isIgnored)

        // requiredIosVersion is satisfied for any newer ios app version
        val ios = Manifest(config = ParserConfig().withAppVersion(DeviceType.IOS, "2"))
        assertFalse(object : Content(ios) {}.isIgnored)
        assertFalse(object : Content(ios, requiredIosVersion = "1".toVersion()) {}.isIgnored)
        assertFalse(object : Content(ios, requiredIosVersion = "2".toVersion()) {}.isIgnored)
        assertTrue(object : Content(ios, requiredIosVersion = "2.0.0.1".toVersion()) {}.isIgnored)
        assertTrue(object : Content(ios, requiredIosVersion = "3".toVersion()) {}.isIgnored)

        // requiredIosVersion is satisfied for any android app version
        val android = Manifest(config = ParserConfig().withAppVersion(DeviceType.ANDROID, "2"))
        assertFalse(object : Content(android, requiredIosVersion = Version.MAX) {}.isIgnored)
    }
    // endregion required-versions

    // region Visibility Attributes
    @Test
    fun verifyGoneIfInvalid() {
        with(object : Content(goneIf = "invalid".toExpressionOrNull()) {}) {
            assertTrue(isIgnored)
        }
    }

    @Test
    fun verifyInvisibleIfInvalid() {
        with(object : Content(invisibleIf = "invalid".toExpressionOrNull()) {}) {
            assertTrue(isIgnored)
        }
    }
    // endregion Visibility Attributes

    // region Parsing
    @Test
    fun parseRestrictTo() = runTest {
        val content = Text(Manifest(), getTestXmlParser("content_restrict_to.xml"))
        assertEquals(setOf(DeviceType.IOS, DeviceType.WEB), content.requiredDeviceType)
    }

    @Test
    fun parseRequiredDeviceType() = runTest {
        val content = Text(Manifest(), getTestXmlParser("content_required_device_type.xml"))
        assertEquals(setOf(DeviceType.ANDROID, DeviceType.WEB), content.requiredDeviceType)
        assertFalse(DeviceType.IOS in content.requiredDeviceType)
    }

    @Test
    fun parseRequiredVersions() = runTest {
        val content = Text(Manifest(), getTestXmlParser("content_required_versions.xml"))
        assertEquals("1.2".toVersion(), content.requiredAndroidVersion)
        assertEquals("2.3".toVersion(), content.requiredIosVersion)
    }

    @Test
    fun parseRequiredVersionsInvalid() = runTest {
        val content = Text(Manifest(), getTestXmlParser("content_required_versions_invalid.xml"))
        assertEquals(Version.MAX, content.requiredAndroidVersion)
        assertEquals(Version.MAX, content.requiredIosVersion)
    }
    // endregion Parsing

    // region parseContentElement()
    @Test
    fun verifyParseContentElementAccordion() = runTest {
        assertIs<Accordion>(getTestXmlParser("accordion.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementAnimation() = runTest {
        assertIs<Animation>(getTestXmlParser("animation.xml").parseContentElement(Manifest()))
        assertIs<Animation>(getTestXmlParser("animation_defaults.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementButton() = runTest {
        assertIs<Button>(getTestXmlParser("button_event.xml").parseContentElement(Manifest()))
        assertIs<Button>(getTestXmlParser("button_restrictTo.xml").parseContentElement(Manifest()))
        assertIs<Button>(getTestXmlParser("button_url.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementCard() = runTest {
        assertIs<Card>(getTestXmlParser("card.xml").parseContentElement(Manifest()))
        assertIs<Card>(getTestXmlParser("card_defaults.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementFallback() = runTest {
        assertIs<Text>(getTestXmlParser("fallback.xml").parseContentElement(Manifest()))
        assertNull(getTestXmlParser("fallback_all_ignored.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementFlow() = runTest {
        assertIs<Flow>(getTestXmlParser("flow.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementForm() = runTest {
        assertIs<Form>(getTestXmlParser("form.xml").parseContentElement(Manifest()))
        assertIs<Form>(getTestXmlParser("form_ignored_content.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementImage() = runTest {
        assertIs<Image>(getTestXmlParser("image.xml").parseContentElement(Manifest()))
        assertIs<Image>(getTestXmlParser("image_restricted.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementInlineTip() = runTest {
        assertIs<InlineTip>(getTestXmlParser("tips/inline_tip.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementInput() = runTest {
        assertIs<Input>(getTestXmlParser("input_email.xml").parseContentElement(Manifest()))
        assertIs<Input>(getTestXmlParser("input_hidden.xml").parseContentElement(Manifest()))
        assertIs<Input>(getTestXmlParser("input_text.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementLink() = runTest {
        assertIs<Link>(getTestXmlParser("link.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementMultiselect() = runTest {
        assertIs<Multiselect>(getTestXmlParser("multiselect.xml").parseContentElement(Manifest()))
        assertIs<Multiselect>(getTestXmlParser("multiselect_defaults.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementParagraph() = runTest {
        assertIs<Paragraph>(getTestXmlParser("paragraph.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementParagraphFallback() = runTest {
        assertIs<Text>(getTestXmlParser("fallback_paragraph.xml").parseContentElement(Manifest()))
        assertNull(getTestXmlParser("fallback_paragraph_all_ignored.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementSpacer() = runTest {
        assertIs<Spacer>(getTestXmlParser("spacer.xml").parseContentElement(Manifest()))
        assertIs<Spacer>(getTestXmlParser("spacer_fixed.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementTabs() = runTest {
        assertIs<Tabs>(getTestXmlParser("tabs_empty.xml").parseContentElement(Manifest()))
        assertIs<Tabs>(getTestXmlParser("tabs_single.xml").parseContentElement(Manifest()))
        assertIs<Tabs>(getTestXmlParser("tabs_multiple.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementText() = runTest {
        assertIs<Text>(getTestXmlParser("text_attributes.xml").parseContentElement(Manifest()))
        assertIs<Text>(getTestXmlParser("text_defaults.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementVideo() = runTest {
        assertIs<Video>(getTestXmlParser("video.xml").parseContentElement(Manifest()))
    }

    @Test
    fun verifyParseContentElementUnrecognized() = runTest {
        assertNull(getTestXmlParser("content_unrecognized.xml").parseContentElement(Manifest()))
    }
    // endregion parseContentElement()
}
