package org.cru.godtools.shared.tool.parser.model.tract

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.ImageScaleType
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Resource
import org.cru.godtools.shared.tool.parser.model.Styles.Companion.DEFAULT_TEXT_SCALE
import org.cru.godtools.shared.tool.parser.model.TEST_GRAVITY
import org.cru.godtools.shared.tool.parser.model.TestColors
import org.cru.godtools.shared.tool.parser.model.page.Page.Companion.DEFAULT_BACKGROUND_COLOR
import org.cru.godtools.shared.tool.parser.model.page.Page.Companion.DEFAULT_BACKGROUND_IMAGE_GRAVITY
import org.cru.godtools.shared.tool.parser.model.page.Page.Companion.DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE
import org.cru.godtools.shared.tool.parser.model.page.backgroundColor
import org.cru.godtools.shared.tool.parser.model.page.backgroundImageGravity
import org.cru.godtools.shared.tool.parser.model.page.backgroundImageScaleType
import org.cru.godtools.shared.tool.parser.model.textColor
import org.cru.godtools.shared.tool.parser.model.toEventIds

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class TractPageTest : UsesResources("model/tract") {
    @Test
    fun verifyParse() = runTest {
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
    fun verifyParseCards() = runTest {
        val page = parsePageXml("page_cards.xml")
        assertEquals(2, page.cards.size)
        assertEquals(TestColors.GREEN, page.cardBackgroundColor)
        with(page.cards[0]) {
            assertEquals("Card 1", label!!.text)
            assertEquals(TestColors.BLACK, backgroundColor)
        }
        with(page.cards[1]) {
            assertEquals("Card 2", label!!.text)
            assertEquals(TestColors.GREEN, backgroundColor)
        }
    }

    @Test
    fun verifyParseModals() = runTest {
        val page = parsePageXml("page_modals.xml")
        assertEquals(2, page.modals.size)
        assertEquals("Modal 1", page.modals[0].title?.text)
        assertEquals("Modal 2", page.modals[1].title?.text)
    }

    @Test
    fun testIsLastPage() {
        val manifest = Manifest(pages = { manifest -> List(3) { TractPage(manifest) } })
        assertEquals(3, manifest.pages.size)
        assertFalse((manifest.pages[0] as TractPage).isLastPage)
        assertFalse((manifest.pages[1] as TractPage).isLastPage)
        assertTrue((manifest.pages[2] as TractPage).isLastPage)
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
        val resource = manifest.getResource("background.png")

        with(null as TractPage?) {
            assertEquals(DEFAULT_BACKGROUND_COLOR, backgroundColor)
            assertEquals(DEFAULT_BACKGROUND_IMAGE_GRAVITY, backgroundImageGravity)
            assertEquals(DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE, backgroundImageScaleType)
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
