package org.cru.godtools.tool.model.tract

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.internal.runBlockingTest
import org.cru.godtools.tool.model.ImageScaleType
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.Resource
import org.cru.godtools.tool.model.Styles.Companion.DEFAULT_TEXT_SCALE
import org.cru.godtools.tool.model.TEST_GRAVITY
import org.cru.godtools.tool.model.TestColors
import org.cru.godtools.tool.model.textColor
import org.cru.godtools.tool.model.toEventIds
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
class TractPageTest : UsesResources("model/tract") {
    @Test
    fun verifyParse() = runBlockingTest {
        val page = parsePageXml("page.xml")
        assertEquals(TestColors.RED, page.backgroundColor)
        assertTrue(page.backgroundImageGravity.isTop)
        assertTrue(page.backgroundImageGravity.isStart)
        assertEquals(ImageScaleType.FILL, page.backgroundImageScaleType)
        assertEquals(1.2345, page.textScale, 0.00001)
        assertEquals("event1 event2".toEventIds().toSet(), page.listeners)
        assertEquals("header", page.header!!.title!!.text)
        assertEquals("hero", page.hero!!.heading!!.text)
        assertEquals("call to action", page.callToAction.label!!.text)
        assertTrue(page.cards.isEmpty())
        assertTrue(page.modals.isEmpty())
    }

    @Test
    fun verifyParseCards() = runBlockingTest {
        val page = parsePageXml("page_cards.xml")
        assertEquals(2, page.cards.size)
        assertEquals("Card 1", page.cards[0].label!!.text)
        assertEquals("Card 2", page.cards[1].label!!.text)
    }

    @Test
    fun verifyParseModals() = runBlockingTest {
        val page = parsePageXml("page_modals.xml")
        assertEquals(2, page.modals.size)
        assertEquals("Modal 1", page.modals[0].title?.text)
        assertEquals("Modal 2", page.modals[1].title?.text)
    }

    @Test
    fun testIsLastPage() {
        val manifest = Manifest(tractPages = { manifest -> List(3) { TractPage(manifest) } })
        assertEquals(3, manifest.tractPages.size)
        assertFalse(manifest.tractPages[0].isLastPage)
        assertFalse(manifest.tractPages[1].isLastPage)
        assertTrue(manifest.tractPages[2].isLastPage)
    }

    @Test
    fun testBackgroundAttributes() {
        val manifest = Manifest(resources = { listOf(Resource(it, name = "background.png")) })
        val page = TractPage(
            manifest,
            backgroundColor = TestColors.GREEN,
            backgroundImage = "background.png",
            backgroundImageGravity = TEST_GRAVITY,
            backgroundImageScaleType = ImageScaleType.FILL_Y
        )
        val resource = manifest.resources["background.png"]

        with(null as TractPage?) {
            assertEquals(TractPage.DEFAULT_BACKGROUND_COLOR, backgroundColor)
            assertEquals(TractPage.DEFAULT_BACKGROUND_IMAGE_GRAVITY, backgroundImageGravity)
            assertEquals(TractPage.DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE, backgroundImageScaleType)
        }
        with(page as TractPage?) {
            assertEquals(TestColors.GREEN, backgroundColor)
            assertEquals(TEST_GRAVITY, backgroundImageGravity)
            assertEquals(ImageScaleType.FILL_Y, backgroundImageScaleType)
        }
        with(page) {
            assertEquals(TestColors.GREEN, backgroundColor)
            assertEquals(resource, backgroundImage)
            assertEquals(TEST_GRAVITY, backgroundImageGravity)
            assertEquals(ImageScaleType.FILL_Y, backgroundImageScaleType)
        }
    }

    @Test
    fun testCardBackgroundColorFallbackBehavior() {
        val manifest = Manifest(cardBackgroundColor = TestColors.BLUE)
        assertEquals(TestColors.GREEN, TractPage(manifest, cardBackgroundColor = TestColors.GREEN).cardBackgroundColor)
        assertEquals(manifest.cardBackgroundColor, TractPage(manifest).cardBackgroundColor)
    }

    @Test
    fun testCardTextColorBehavior() {
        with(TractPage(textColor = TestColors.GREEN)) {
            assertEquals(TestColors.GREEN, cardTextColor)
            assertEquals(textColor, cardTextColor)
        }
        with(TractPage(cardTextColor = TestColors.GREEN)) {
            assertEquals(TestColors.GREEN, cardTextColor)
            assertNotEquals(textColor, cardTextColor)
        }
    }

    @Test
    fun testTextColor() {
        with(null as TractPage?) { assertEquals(Manifest.DEFAULT_TEXT_COLOR, textColor) }
        with(TractPage(Manifest(textColor = TestColors.GREEN))) {
            assertEquals(TestColors.GREEN, (this as TractPage?).textColor)
            assertEquals(TestColors.GREEN, textColor)
        }
        with(TractPage(Manifest(textColor = TestColors.RED), textColor = TestColors.GREEN)) {
            assertEquals(TestColors.GREEN, (this as TractPage?).textColor)
            assertEquals(TestColors.GREEN, textColor)
        }
    }

    @Test
    fun testTextScale() {
        assertEquals(DEFAULT_TEXT_SCALE, TractPage(Manifest()).textScale, 0.001)
        assertEquals(2.0, TractPage(textScale = 2.0).textScale, 0.001)

        val manifest = Manifest(textScale = 3.0)
        assertEquals(3.0, TractPage(manifest).textScale, 0.001)
        assertEquals(6.0, TractPage(manifest, textScale = 2.0).textScale, 0.001)
    }

    private suspend fun parsePageXml(file: String) = TractPage(Manifest(), null, getTestXmlParser(file))
}
