package org.cru.godtools.tool.model.tract

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.model.ImageScaleType
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.TestColors
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
class TractPageTest : UsesResources("model/tract") {
    @Test
    fun verifyParse() {
        val page = parsePageXml("page.xml")
        assertEquals(TestColors.RED, page.backgroundColor)
        assertTrue(page.backgroundImageGravity.isTop)
        assertTrue(page.backgroundImageGravity.isStart)
        assertEquals(ImageScaleType.FILL, page.backgroundImageScaleType)
        assertEquals(1.2345, page.textScale, 0.00001)
        assertEquals("header", page.header!!.title!!.text)
        assertEquals("hero", page.hero!!.heading!!.text)
        assertEquals("call to action", page.callToAction.label!!.text)
        assertTrue(page.modals.isEmpty())
    }

    @Test
    fun verifyParseModals() {
        val page = parsePageXml("page_modals.xml")
        assertEquals(2, page.modals.size)
        assertEquals("Modal 1", page.modals[0].title?.text)
        assertEquals("Modal 2", page.modals[1].title?.text)
    }

    @Test
    fun testCardBackgroundColorFallbackBehavior() {
        val manifest = Manifest(cardBackgroundColor = TestColors.BLUE)
        assertEquals(TestColors.GREEN, TractPage(manifest, cardBackgroundColor = TestColors.GREEN).cardBackgroundColor)
        assertEquals(manifest.cardBackgroundColor, TractPage(manifest).cardBackgroundColor)
    }

    @Test
    fun testTextScale() {
        assertEquals(TractPage.DEFAULT_TEXT_SCALE, TractPage(Manifest()).textScale, 0.001)
        assertEquals(2.0, TractPage(Manifest(), textScale = 2.0).textScale, 0.001)

        val manifest = Manifest(textScale = 3.0)
        assertEquals(3.0, TractPage(manifest).textScale, 0.001)
        assertEquals(6.0, TractPage(manifest, textScale = 2.0).textScale, 0.001)
    }

    private fun parsePageXml(file: String) = TractPage(Manifest(), 0, null, getTestXmlParser(file))
}
