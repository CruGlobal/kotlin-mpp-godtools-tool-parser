package org.cru.godtools.shared.tool.parser.model.lesson

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.ImageScaleType
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Resource
import org.cru.godtools.shared.tool.parser.model.Styles.Companion.DEFAULT_TEXT_SCALE
import org.cru.godtools.shared.tool.parser.model.TEST_GRAVITY
import org.cru.godtools.shared.tool.parser.model.TestColors
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.page.Page.Companion.DEFAULT_BACKGROUND_COLOR
import org.cru.godtools.shared.tool.parser.model.page.Page.Companion.DEFAULT_BACKGROUND_IMAGE_GRAVITY
import org.cru.godtools.shared.tool.parser.model.page.Page.Companion.DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE
import org.cru.godtools.shared.tool.parser.model.page.backgroundColor
import org.cru.godtools.shared.tool.parser.model.page.backgroundImageGravity
import org.cru.godtools.shared.tool.parser.model.page.backgroundImageScaleType
import org.cru.godtools.shared.tool.parser.model.toEventIds
import org.cru.godtools.shared.tool.parser.model.toPlatformColor

@RunOnAndroidWith(AndroidJUnit4::class)
class LessonPageTest : UsesResources("model/lesson") {
    // region parsePage
    @Test
    fun testParsePage() = runTest {
        val manifest = Manifest(resources = { listOf(Resource(it, "background.png")) })
        val page = parsePageXml("page.xml", manifest)
        assertFalse(page.isHidden)
        assertEquals(TestColors.GREEN, page.controlColor)
        assertEquals(1.2345, page.textScale, 0.00001)
        assertEquals(1, page.content.size)
        assertEquals(AnalyticsEvent.System.FIREBASE, page.analyticsEvents.single().systems.single())
        assertIs<Text>(page.content[0])
        assertNotNull(page.backgroundImage) { assertEquals(manifest.getResource("background.png"), it) }
        assertEquals(TestColors.RED, page.multiselectOptionBackgroundColor)
        assertEquals(TestColors.GREEN, page.multiselectOptionSelectedColor)
        assertEquals(TestColors.RED.toPlatformColor(), page.backgroundColor)
        assertTrue(page.backgroundImageGravity.isTop)
        assertTrue(page.backgroundImageGravity.isEnd)
        assertEquals(ImageScaleType.FIT, page.backgroundImageScaleType)
        assertEquals("lesson_page_event1".toEventIds().toSet(), page.listeners)
    }

    @Test
    fun testParsePageDefault() = runTest {
        val manifest = Manifest()
        val page = parsePageXml("page_defaults.xml", manifest)
        assertEquals(manifest.pageControlColor, page.controlColor)
        assertEquals(manifest.multiselectOptionBackgroundColor, page.multiselectOptionBackgroundColor)
        assertEquals(manifest.multiselectOptionSelectedColor, page.multiselectOptionSelectedColor)
        assertEquals(DEFAULT_TEXT_SCALE, page.textScale, 0.001)
        assertTrue(page.analyticsEvents.isEmpty())
        assertTrue(page.content.isEmpty())
    }

    @Test
    fun testParsePageHidden() = runTest {
        val page = parsePageXml("page_hidden.xml")
        assertTrue(page.isHidden)
    }

    private suspend fun parsePageXml(file: String, manifest: Manifest = Manifest()) =
        LessonPage(manifest, null, getTestXmlParser(file))
    // endregion parsePage

    // region Attribute Behavior
    @Test
    fun testBackgroundColor() {
        assertEquals(DEFAULT_BACKGROUND_COLOR, (null as LessonPage?).backgroundColor)

        val page = LessonPage(Manifest(), backgroundColor = TestColors.GREEN.toPlatformColor())
        assertEquals(TestColors.GREEN.toPlatformColor(), page.backgroundColor)
        assertEquals(TestColors.GREEN.toPlatformColor(), (page as LessonPage?).backgroundColor)
    }

    @Test
    fun testBackgroundImageAttrs() {
        val nullPage = null as LessonPage?
        assertEquals(DEFAULT_BACKGROUND_IMAGE_GRAVITY, nullPage.backgroundImageGravity)
        assertEquals(DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE, nullPage.backgroundImageScaleType)

        val manifest = Manifest(resources = { listOf(Resource(it, "test.png")) })
        val resource = manifest.getResource("test.png")
        val page = LessonPage(
            manifest,
            backgroundImage = "test.png",
            backgroundImageGravity = TEST_GRAVITY,
            backgroundImageScaleType = ImageScaleType.FIT
        )
        assertSame(resource, page.backgroundImage)
        assertEquals(TEST_GRAVITY, (page as LessonPage?).backgroundImageGravity)
        assertEquals(ImageScaleType.FIT, (page as LessonPage?).backgroundImageScaleType)
    }

    @Test
    fun testControlColor() {
        assertEquals(TestColors.GREEN, LessonPage(controlColor = TestColors.GREEN).controlColor)
        assertEquals(TestColors.GREEN, LessonPage(Manifest(pageControlColor = TestColors.GREEN)).controlColor)
        assertEquals(
            TestColors.GREEN,
            LessonPage(Manifest(pageControlColor = TestColors.RED), controlColor = TestColors.GREEN).controlColor
        )
    }

    @Test
    fun testTextScale() {
        assertEquals(DEFAULT_TEXT_SCALE, LessonPage(Manifest()).textScale, 0.001)
        assertEquals(2.0, LessonPage(Manifest(), textScale = 2.0).textScale, 0.001)

        val manifest = Manifest(textScale = 3.0)
        assertEquals(3.0, LessonPage(manifest).textScale, 0.001)
        assertEquals(6.0, LessonPage(manifest, textScale = 2.0).textScale, 0.001)
    }
    // endregion Attribute Behavior

    @Test
    fun testGetAnalyticsEvents() {
        val defaultEvent = AnalyticsEvent(trigger = AnalyticsEvent.Trigger.DEFAULT)
        val visibleEvent = AnalyticsEvent(trigger = AnalyticsEvent.Trigger.VISIBLE)
        val hiddenEvent = AnalyticsEvent(trigger = AnalyticsEvent.Trigger.HIDDEN)
        val page = LessonPage(analyticsEvents = listOf(defaultEvent, visibleEvent, hiddenEvent))

        assertEquals(listOf(defaultEvent, visibleEvent), page.getAnalyticsEvents(AnalyticsEvent.Trigger.VISIBLE))
        assertEquals(listOf(hiddenEvent), page.getAnalyticsEvents(AnalyticsEvent.Trigger.HIDDEN))
        assertFailsWith(IllegalStateException::class) { page.getAnalyticsEvents(AnalyticsEvent.Trigger.DEFAULT) }
    }
}
