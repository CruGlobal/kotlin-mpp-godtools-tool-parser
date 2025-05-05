package org.cru.godtools.shared.tool.parser.model.tract

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.ImageScaleType
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Paragraph
import org.cru.godtools.shared.tool.parser.model.Resource
import org.cru.godtools.shared.tool.parser.model.TEST_GRAVITY
import org.cru.godtools.shared.tool.parser.model.TestColors
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.tips.InlineTip
import org.cru.godtools.shared.tool.parser.model.tips.Tip
import org.cru.godtools.shared.tool.parser.model.toEventIds
import org.cru.godtools.shared.tool.parser.model.toPlatformColor
import org.cru.godtools.shared.tool.parser.model.tract.TractPage.Card

@RunOnAndroidWith(AndroidJUnit4::class)
class TractPageCardTest : UsesResources("model/tract") {
    @Test
    fun verifyParseCard() = runTest {
        val card = TractPage(Manifest(code = "test"), "page.xml", getTestXmlParser("card.xml")).cards.single()
        assertEquals("page.xml-0", card.id)
        assertEquals("Card 1", card.label!!.text)
        assertEquals(card.primaryColor, card.label!!.textColor)
        assertNotEquals(card.textColor, card.label!!.textColor)
        assertEquals(TestColors.RED.toPlatformColor(), card.backgroundColor)
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
            backgroundColor = TestColors.GREEN.toPlatformColor(),
            backgroundImage = "background.png",
            backgroundImageGravity = TEST_GRAVITY,
            backgroundImageScaleType = ImageScaleType.FILL_Y
        )
        val resource = manifest.getResource("background.png")

        with(null as Card?) {
            assertEquals(manifest.backgroundColor, backgroundColor)
            assertEquals(Card.DEFAULT_BACKGROUND_IMAGE_GRAVITY, backgroundImageGravity)
            assertEquals(Card.DEFAULT_BACKGROUND_IMAGE_SCALE_TYPE, backgroundImageScaleType)
        }
        with(card as Card?) {
            assertEquals(TestColors.GREEN.toPlatformColor(), backgroundColor)
            assertEquals(TEST_GRAVITY, backgroundImageGravity)
            assertEquals(ImageScaleType.FILL_Y, backgroundImageScaleType)
        }
        with(card) {
            assertEquals(TestColors.GREEN.toPlatformColor(), backgroundColor)
            assertEquals(resource, backgroundImage)
            assertEquals(TEST_GRAVITY, backgroundImageGravity)
            assertEquals(ImageScaleType.FILL_Y, backgroundImageScaleType)
        }
    }

    @Test
    fun testCardBackgroundColorFallbackBehavior() {
        val page = TractPage(cardBackgroundColor = TestColors.GREEN.toPlatformColor())
        assertEquals(TestColors.GREEN.toPlatformColor(), Card(page).backgroundColor)
        assertEquals(
            TestColors.BLUE.toPlatformColor(),
            Card(page, backgroundColor = TestColors.BLUE.toPlatformColor()).backgroundColor,
        )
    }

    @Test
    fun testAnalyticsEvents() {
        val defaultEvent = AnalyticsEvent(trigger = AnalyticsEvent.Trigger.DEFAULT)
        val visibleEvent = AnalyticsEvent(trigger = AnalyticsEvent.Trigger.VISIBLE)
        val hiddenEvent = AnalyticsEvent(trigger = AnalyticsEvent.Trigger.HIDDEN)
        val card = Card(analyticsEvents = listOf(defaultEvent, visibleEvent, hiddenEvent))

        assertEquals(listOf(defaultEvent, visibleEvent), card.getAnalyticsEvents(AnalyticsEvent.Trigger.VISIBLE))
        assertEquals(listOf(hiddenEvent), card.getAnalyticsEvents(AnalyticsEvent.Trigger.HIDDEN))
        assertFailsWith(IllegalStateException::class) { card.getAnalyticsEvents(AnalyticsEvent.Trigger.DEFAULT) }
    }

    @Test
    fun testLabelTextColor() {
        with(Card(label = { Text(it) })) {
            assertEquals(primaryColor, label!!.textColor)
            assertNotEquals(textColor, label!!.textColor)
        }

        with(Card(label = { Text(it, textColor = TestColors.GREEN.toPlatformColor()) })) {
            assertEquals(TestColors.GREEN.toPlatformColor(), label!!.textColor)
            assertNotEquals(primaryColor, label!!.textColor)
            assertNotEquals(textColor, label!!.textColor)
        }
    }
}
