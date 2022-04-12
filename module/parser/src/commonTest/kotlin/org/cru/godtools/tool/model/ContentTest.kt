package org.cru.godtools.tool.model

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.cru.godtools.expressions.toExpressionOrNull
import org.cru.godtools.tool.FEATURE_ANIMATION
import org.cru.godtools.tool.FEATURE_MULTISELECT
import org.cru.godtools.tool.LegacyParserConfig
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.internal.coroutines.receive
import org.cru.godtools.tool.model.Content.Companion.parseContentElement
import org.cru.godtools.tool.model.tips.InlineTip
import org.cru.godtools.tool.state.State
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ContentTest : UsesResources() {
    private val state = State()

    // region required-features
    @Test
    fun verifyRequiredFeaturesSupported() {
        LegacyParserConfig.supportedFeatures = setOf(FEATURE_ANIMATION, FEATURE_MULTISELECT)
        assertFalse(object : Content(requiredFeatures = setOf(FEATURE_ANIMATION, FEATURE_MULTISELECT)) {}.testIsIgnored)
        assertFalse(object : Content(requiredFeatures = setOf(FEATURE_ANIMATION)) {}.testIsIgnored)
        assertFalse(object : Content(requiredFeatures = setOf(FEATURE_MULTISELECT)) {}.testIsIgnored)
        assertFalse(object : Content(requiredFeatures = emptySet()) {}.testIsIgnored)
    }

    @Test
    fun verifyRequiredFeaturesNotSupported() {
        LegacyParserConfig.supportedFeatures = setOf(FEATURE_ANIMATION)
        assertTrue(object : Content(requiredFeatures = setOf(FEATURE_ANIMATION, FEATURE_MULTISELECT)) {}.testIsIgnored)
        assertTrue(object : Content(requiredFeatures = setOf(FEATURE_MULTISELECT)) {}.testIsIgnored)
        assertTrue(object : Content(requiredFeatures = setOf("kjlasdf")) {}.testIsIgnored)
    }
    // endregion required-features

    // region restrictTo
    @Test
    fun verifyRestrictToSupported() {
        LegacyParserConfig.supportedDeviceTypes = setOf(DeviceType.ANDROID)
        assertFalse(object : Content(Manifest(), restrictTo = DeviceType.ALL) {}.testIsIgnored)
        assertFalse(object : Content(Manifest(), restrictTo = LegacyParserConfig.supportedDeviceTypes) {}.testIsIgnored)
        assertFalse(object : Content(Manifest(), restrictTo = setOf(DeviceType.ANDROID)) {}.testIsIgnored)
    }

    @Test
    fun verifyRestrictToNotSupported() {
        LegacyParserConfig.supportedDeviceTypes = setOf(DeviceType.ANDROID)
        assertTrue(object : Content(Manifest(), restrictTo = setOf(DeviceType.UNKNOWN)) {}.testIsIgnored)
        assertTrue(object : Content(Manifest(), restrictTo = setOf(DeviceType.IOS)) {}.testIsIgnored)
    }
    // endregion restrictTo

    // region version
    @Test
    fun verifyVersionSupported() {
        assertFalse(object : Content(Manifest(), version = SCHEMA_VERSION) {}.testIsIgnored)
    }

    @Test
    fun verifyVersionNotSupported() {
        assertTrue(object : Content(Manifest(), version = SCHEMA_VERSION + 1) {}.testIsIgnored)
    }
    // endregion version

    // region Visibility Attributes
    @Test
    fun verifyGoneIfInvalid() {
        with(object : Content(goneIf = "invalid".toExpressionOrNull()) {}) {
            assertTrue(testIsIgnored)
        }
    }

    @Test
    fun verifyIsGone() {
        with(object : Content(goneIf = "isSet(a)".toExpressionOrNull()) {}) {
            assertFalse(isGone(state))
            state["a"] = "test"
            assertTrue(isGone(state))
        }
    }

    @Test
    fun verifyIsGoneFlow() = runTest {
        with(object : Content(goneIf = "isSet(a) || isSet(b)".toExpressionOrNull()) {}) {
            val output = Channel<Boolean>(1)
            val flow = isGoneFlow(state)
                .onEach { output.send(it) }
                .launchIn(this@runTest)
            assertFalse("Initially not hidden") { output.receive(500) }

            state["c"] = "test"
            delay(100)
            assertTrue(output.isEmpty, "'c' should have no effect on isHidden result")

            state["a"] = "test"
            assertTrue(output.receive(500), "'a' is now set")

            state["a"] = emptyList()
            assertFalse(output.receive(500), "'a' is no longer set")

            state["b"] = "test"
            assertTrue(output.receive(500), "'b' is now set")

            state["a"] = "test"
            delay(100)
            assertTrue(output.isEmpty, "'a' is now set, but shouldn't change isHidden result")

            state["b"] = emptyList()
            delay(100)
            assertTrue(output.isEmpty, "'a' is still set, so isHidden result shouldn't change")

            state["a"] = emptyList()
            assertFalse(output.receive(500), "'a' is no longer set")

            flow.cancel()
        }
    }

    @Test
    fun verifyIsGoneDefault() = runTest {
        with(object : Content() {}) {
            val output = Channel<Boolean>(1)
            val flow = isGoneFlow(state)
                .onEach { output.send(it) }
                .launchIn(this@runTest)
            assertFalse("Initially not hidden") { output.receive(500) }
            assertFalse(isGone(state))

            for (i in 1..10) {
                state["a$i"] = "test"
                assertFalse(isGone(state))
                delay(100)
                assertTrue(output.isEmpty)
            }

            flow.cancel()
        }
    }

    @Test
    fun verifyInvisibleIfInvalid() {
        with(object : Content(invisibleIf = "invalid".toExpressionOrNull()) {}) {
            assertTrue(testIsIgnored)
        }
    }

    @Test
    fun verifyIsInvisible() {
        with(object : Content(invisibleIf = "isSet(a)".toExpressionOrNull()) {}) {
            assertFalse(isInvisible(state))
            state["a"] = "test"
            assertTrue(isInvisible(state))
        }
    }

    @Test
    fun verifyIsInvisibleFlow() = runTest {
        with(object : Content(invisibleIf = "isSet(a) || isSet(b)".toExpressionOrNull()) {}) {
            val output = Channel<Boolean>(1)
            val flow = isInvisibleFlow(state)
                .onEach { output.send(it) }
                .launchIn(this@runTest)
            assertFalse("Initially not invisible") { output.receive(500) }

            state["c"] = "test"
            delay(100)
            assertTrue(output.isEmpty, "'c' should have no effect on isInvisible result")

            state["a"] = "test"
            assertTrue(output.receive(500), "'a' is now set")

            state["a"] = emptyList()
            assertFalse(output.receive(500), "'a' is no longer set")

            state["b"] = "test"
            assertTrue(output.receive(500), "'b' is now set")

            state["a"] = "test"
            delay(100)
            assertTrue(output.isEmpty, "'a' is now set, but shouldn't change isInvisible result")

            state["b"] = emptyList()
            delay(100)
            assertTrue(output.isEmpty, "'a' is still set, so isInvisible result shouldn't change")

            state["a"] = emptyList()
            assertFalse(output.receive(500), "'a' is no longer set")

            flow.cancel()
        }
    }

    @Test
    fun verifyIsInvisibleDefault() = runTest {
        with(object : Content() {}) {
            val output = Channel<Boolean>(1)
            val flow = isInvisibleFlow(state)
                .onEach { output.send(it) }
                .launchIn(this@runTest)
            assertFalse("Initially not invisible") { output.receive(500) }
            assertFalse(isInvisible(state))

            for (i in 1..10) {
                state["a$i"] = "test"
                assertFalse(isInvisible(state))
                delay(100)
                assertTrue(output.isEmpty)
            }

            flow.cancel()
        }
    }
    // endregion Visibility Attributes

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
