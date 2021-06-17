package org.cru.godtools.tool.model.tract

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.model.ImageScaleType
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.Paragraph
import org.cru.godtools.tool.model.Resource
import org.cru.godtools.tool.model.TEST_GRAVITY
import org.cru.godtools.tool.model.TEXT_SIZE_BASE
import org.cru.godtools.tool.model.TEXT_SIZE_CARD_LABEL
import org.cru.godtools.tool.model.TestColors
import org.cru.godtools.tool.model.Text
import org.cru.godtools.tool.model.tips.InlineTip
import org.cru.godtools.tool.model.tips.Tip
import org.cru.godtools.tool.model.toEventIds
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
class CardTest : UsesResources("model/tract") {
    @Test
    fun verifyParseCard() {
        val card = TractPage(Manifest(code = "test"), "page.xml", getTestXmlParser("card.xml")).cards.single()
        assertEquals("page.xml-0", card.id)
        assertEquals("Card 1", card.label!!.text)
        assertEquals(TestColors.RED, card.backgroundColor)
        assertEquals("listener1 listener2".toEventIds().toSet(), card.listeners)
        assertEquals("dismiss-listener1 dismiss-listener2".toEventIds().toSet(), card.dismissListeners)
        assertEquals(1, card.analyticsEvents.size)
        assertEquals("firebaseEvent", card.analyticsEvents.single().action)
        assertEquals(1, card.content.size)
        assertIs<Paragraph>(card.content[0])
    }

    @Test
    fun verifyCardTips() {
        val tip1 = Tip(id = "tip1")
        val tip2 = Tip(id = "tip2")
        val page = TractPage(Manifest(tips = { listOf(tip1, tip2) }))
        val card = Card(page) {
            listOf(
                InlineTip(it, "tip1"),
                Paragraph(it) { listOf(InlineTip(it, "tip2")) },
                InlineTip(it, "tip3"),
                InlineTip(it, "tip1")
            )
        }

        assertEquals(3, card.tips.size)
        assertEquals(listOf(tip1, tip2, tip1), card.tips)
    }

    @Test
    fun verifyCardIsLastVisibleCard() {
        val page = TractPage(
            cards = { listOf(Card(it, isHidden = false), Card(it, isHidden = false), Card(it, isHidden = true)) }
        )

        assertFalse(page.cards[0].isLastVisibleCard)
        assertTrue(page.cards[1].isLastVisibleCard)
        assertFalse(page.cards[2].isLastVisibleCard)
    }

    @Test
    fun testBackgroundAttributes() {
        val manifest = Manifest(resources = { listOf(Resource(it, name = "background.png")) })
        val card = Card(
            TractPage(manifest),
            backgroundColor = TestColors.GREEN,
            backgroundImage = "background.png",
            backgroundImageGravity = TEST_GRAVITY,
            backgroundImageScaleType = ImageScaleType.FILL_Y
        )
        val resource = manifest.resources["background.png"]

        with(null as Card?) {
            assertEquals(manifest.backgroundColor, backgroundColor)
            assertEquals(Card.DEFAULT_BACKGROUND_IMAGE_GRAVITY, backgroundImageGravity)
            assertEquals(Card.DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE, backgroundImageScaleType)
        }
        with(card as Card?) {
            assertEquals(TestColors.GREEN, backgroundColor)
            assertEquals(TEST_GRAVITY, backgroundImageGravity)
            assertEquals(ImageScaleType.FILL_Y, backgroundImageScaleType)
        }
        with(card) {
            assertEquals(TestColors.GREEN, backgroundColor)
            assertEquals(resource, backgroundImage)
            assertEquals(TEST_GRAVITY, backgroundImageGravity)
            assertEquals(ImageScaleType.FILL_Y, backgroundImageScaleType)
        }
    }

    @Test
    fun testCardBackgroundColorFallbackBehavior() {
        val page = TractPage(cardBackgroundColor = TestColors.GREEN)
        assertEquals(TestColors.GREEN, Card(page).backgroundColor)
        assertEquals(TestColors.BLUE, Card(page, backgroundColor = TestColors.BLUE).backgroundColor)
    }

    @Test
    fun testLabelTextSize() {
        with(Card(label = { Text(it) })) {
            assertEquals(TEXT_SIZE_CARD_LABEL, (label!!.textScale * TEXT_SIZE_BASE).toInt())
        }

        with(Card(TractPage(textScale = 2.0), label = { Text(it) })) {
            assertEquals(2 * TEXT_SIZE_CARD_LABEL, (label!!.textScale * TEXT_SIZE_BASE).toInt())
        }

        with(Card(label = { Text(it, textScale = 2.0) })) {
            assertEquals(2 * TEXT_SIZE_CARD_LABEL, (label!!.textScale * TEXT_SIZE_BASE).toInt())
        }
    }
}
