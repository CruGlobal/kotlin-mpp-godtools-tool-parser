package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.ParserConfig
import org.cru.godtools.shared.tool.parser.ParserConfig.Companion.FEATURE_CONTENT_CARD
import org.cru.godtools.shared.tool.parser.internal.UsesResources

@RunOnAndroidWith(AndroidJUnit4::class)
class CardTest : UsesResources() {
    @Test
    fun testParseCard() = runTest {
        val card = Card(Manifest(), getTestXmlParser("card.xml"))
        assertEquals(TestColors.RED, card.backgroundColor)
        assertTrue(card.isClickable)
        assertEquals("https://www.example.com", card.url.toString())
        assertEquals(2, card.content.size)
        assertIs<Spacer>(card.content[0])
        assertIs<Text>(card.content[1])
    }

    @Test
    fun testParseCardDefaults() = runTest {
        val card = Card(Manifest(), getTestXmlParser("card_defaults.xml"))
        assertEquals(Manifest.DEFAULT_BACKGROUND_COLOR, card.backgroundColor)
        assertFalse(card.isClickable)
        assertTrue(card.content.isEmpty())
    }

    @Test
    fun testIsIgnored() {
        with(Card(Manifest(config = ParserConfig().withSupportedFeatures(FEATURE_CONTENT_CARD)))) {
            assertFalse(isIgnored)
        }
        with(Card(Manifest(config = ParserConfig().withSupportedFeatures()))) {
            assertTrue(isIgnored)
        }
    }

    @Test
    fun testCardBackgroundColorFallbackBehavior() {
        val parent = object : BaseModel(), Styles {
            override val multiselectOptionBackgroundColor = TestColors.RANDOM
        }
        assertEquals(parent.cardBackgroundColor, Card(parent).backgroundColor)
        assertEquals(TestColors.GREEN, Card(parent, backgroundColor = TestColors.GREEN).backgroundColor)
    }
}
