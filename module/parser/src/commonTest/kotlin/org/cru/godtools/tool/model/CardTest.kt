package org.cru.godtools.tool.model

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.cru.godtools.tool.FEATURE_CONTENT_CARD
import org.cru.godtools.tool.ParserConfig
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
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
        with(Card(Manifest(config = ParserConfig(supportedFeatures = setOf(FEATURE_CONTENT_CARD))))) {
            assertFalse(isIgnored)
        }
        with(Card(Manifest(config = ParserConfig(supportedFeatures = emptySet())))) {
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
        assertEquals(Manifest.DEFAULT_BACKGROUND_COLOR, (null as Card?).backgroundColor)
    }
}
