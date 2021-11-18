package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.internal.runBlockingTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
class CardTest : UsesResources() {
    @Test
    fun testParseCard() = runBlockingTest {
        val card = Card(Manifest(), getTestXmlParser("card.xml"))
        assertEquals(TestColors.RED, card.backgroundColor)
        assertTrue(card.isClickable)
        assertEquals("https://www.example.com", card.url.toString())
        assertEquals(2, card.content.size)
        assertIs<Spacer>(card.content[0])
        assertIs<Text>(card.content[1])
    }

    @Test
    fun testParseCardDefaults() = runBlockingTest {
        val card = Card(Manifest(), getTestXmlParser("card_defaults.xml"))
        assertEquals(Manifest.DEFAULT_BACKGROUND_COLOR, card.backgroundColor)
        assertFalse(card.isClickable)
        assertTrue(card.content.isEmpty())
    }
}
