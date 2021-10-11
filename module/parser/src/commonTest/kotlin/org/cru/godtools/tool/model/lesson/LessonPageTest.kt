package org.cru.godtools.tool.model.lesson

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.internal.runBlockingTest
import org.cru.godtools.tool.model.AnalyticsEvent
import org.cru.godtools.tool.model.ImageScaleType
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.Resource
import org.cru.godtools.tool.model.Styles.Companion.DEFAULT_TEXT_SCALE
import org.cru.godtools.tool.model.TEST_GRAVITY
import org.cru.godtools.tool.model.TestColors
import org.cru.godtools.tool.model.Text
import org.cru.godtools.tool.model.page.DEFAULT_CONTROL_COLOR
import org.cru.godtools.tool.model.page.Page.Companion.DEFAULT_BACKGROUND_COLOR
import org.cru.godtools.tool.model.page.Page.Companion.DEFAULT_BACKGROUND_IMAGE_GRAVITY
import org.cru.godtools.tool.model.page.Page.Companion.DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE
import org.cru.godtools.tool.model.page.backgroundColor
import org.cru.godtools.tool.model.page.backgroundImageGravity
import org.cru.godtools.tool.model.page.backgroundImageScaleType
import org.cru.godtools.tool.model.page.controlColor
import org.cru.godtools.tool.model.toEventIds
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
class LessonPageTest : UsesResources("model/lesson") {
    // region parsePage
    @Test
    fun testParsePage() = runBlockingTest {
        val page = parsePageXml("page.xml")
        assertFalse(page.isHidden)
        assertEquals(TestColors.GREEN, page.controlColor)
        assertEquals(1.2345, page.textScale, 0.00001)
        assertEquals(1, page.content.size)
        assertEquals(AnalyticsEvent.System.APPSFLYER, page.analyticsEvents.single().systems.single())
        assertIs<Text>(page.content[0])
        assertEquals("background.png", page._backgroundImage)
        assertEquals(TestColors.RED, page.multiselectOptionBackgroundColor)
        assertEquals(TestColors.GREEN, page.multiselectOptionSelectedColor)
        assertEquals(TestColors.RED, page.backgroundColor)
        assertTrue(page.backgroundImageGravity.isTop)
        assertTrue(page.backgroundImageGravity.isEnd)
        assertEquals(ImageScaleType.FIT, page.backgroundImageScaleType)
        assertEquals("lesson_page_event1".toEventIds().toSet(), page.listeners)
    }

    @Test
    fun testParsePageDefault() = runBlockingTest {
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
    fun testParsePageHidden() = runBlockingTest {
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

        val page = LessonPage(Manifest(), backgroundColor = TestColors.GREEN)
        assertEquals(TestColors.GREEN, page.backgroundColor)
        assertEquals(TestColors.GREEN, (page as LessonPage?).backgroundColor)
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
        assertEquals(DEFAULT_CONTROL_COLOR, (null as LessonPage?).controlColor)

        with(LessonPage(Manifest(), controlColor = TestColors.GREEN)) {
            assertEquals(TestColors.GREEN, controlColor)
            assertEquals(TestColors.GREEN, (this as LessonPage?).controlColor)
        }

        with(LessonPage(Manifest(pageControlColor = TestColors.GREEN))) {
            assertEquals(TestColors.GREEN, controlColor)
            assertEquals(TestColors.GREEN, (this as LessonPage?).controlColor)
        }
        with(LessonPage(Manifest(pageControlColor = TestColors.RED), controlColor = TestColors.GREEN)) {
            assertEquals(TestColors.GREEN, controlColor)
            assertEquals(TestColors.GREEN, (this as LessonPage?).controlColor)
        }
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
        val selectedEvent = AnalyticsEvent(trigger = AnalyticsEvent.Trigger.SELECTED)
        val page = LessonPage(analyticsEvents = listOf(defaultEvent, visibleEvent, hiddenEvent, selectedEvent))

        assertEquals(listOf(defaultEvent, visibleEvent), page.getAnalyticsEvents(AnalyticsEvent.Trigger.VISIBLE))
        assertEquals(listOf(hiddenEvent), page.getAnalyticsEvents(AnalyticsEvent.Trigger.HIDDEN))
        assertFailsWith(IllegalStateException::class) { page.getAnalyticsEvents(AnalyticsEvent.Trigger.DEFAULT) }
        assertFailsWith(IllegalStateException::class) { page.getAnalyticsEvents(AnalyticsEvent.Trigger.SELECTED) }
    }
}
