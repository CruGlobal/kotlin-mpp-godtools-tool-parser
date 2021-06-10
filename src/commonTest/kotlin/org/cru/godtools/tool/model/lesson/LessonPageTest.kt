package org.cru.godtools.tool.model.lesson

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.model.ImageGravity
import org.cru.godtools.tool.model.ImageScaleType
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.TestColors
import org.cru.godtools.tool.model.Text
import org.cru.godtools.tool.model.lesson.LessonPage.Companion.DEFAULT_TEXT_SCALE
import org.cru.godtools.tool.model.toEventIds
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
class LessonPageTest : UsesResources("model/lesson") {
    // region parsePage
    @Test
    fun testParsePage() {
        val page = parsePageXml("page.xml")
        assertFalse(page.isHidden)
        assertEquals(TestColors.GREEN, page.controlColor)
        assertEquals(1.2345, page.textScale, 0.00001)
        assertEquals(1, page.content.size)
        assertIs<Text>(page.content[0])
        assertEquals("background.png", page._backgroundImage)
        assertEquals(TestColors.RED, page.backgroundColor)
        assertEquals(ImageGravity.TOP or ImageGravity.END, page.backgroundImageGravity)
        assertEquals(ImageScaleType.FIT, page.backgroundImageScaleType)
        assertEquals("lesson_page_event1".toEventIds().toSet(), page.listeners)
    }

    @Test
    fun testParsePageDefault() {
        val manifest = Manifest()
        val page = parsePageXml("page_defaults.xml", manifest)
        assertEquals(manifest.lessonControlColor, page.controlColor)
        assertEquals(DEFAULT_TEXT_SCALE, page.textScale, 0.001)
        assertTrue(page.content.isEmpty())
    }

    @Test
    fun testParsePageHidden() {
        val page = parsePageXml("page_hidden.xml")
        assertTrue(page.isHidden)
    }
    // endregion parsePage

    @Test
    fun testTextScale() {
        assertEquals(DEFAULT_TEXT_SCALE, LessonPage(Manifest()).textScale, 0.001)
        assertEquals(2.0, LessonPage(Manifest(), textScale = 2.0).textScale, 0.001)

        val manifest = Manifest(textScale = 3.0)
        assertEquals(3.0, LessonPage(manifest).textScale, 0.001)
        assertEquals(6.0, LessonPage(manifest, textScale = 2.0).textScale, 0.001)
    }

    private fun parsePageXml(file: String, manifest: Manifest = Manifest()) =
        LessonPage(manifest, null, getTestXmlParser(file))
}
