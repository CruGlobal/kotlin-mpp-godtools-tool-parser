package org.cru.godtools.shared.tool.parser.model.page

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent.Trigger
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Paragraph
import org.cru.godtools.shared.tool.parser.model.Spacer
import org.cru.godtools.shared.tool.parser.model.TestColors
import org.cru.godtools.shared.tool.parser.xml.XmlPullParserException

@RunOnAndroidWith(AndroidJUnit4::class)
class CardCollectionPageTest : UsesResources("model/page") {
    // region Parse XML
    @Test
    fun testParseCardCollectionPage() = runTest {
        with(CardCollectionPage(Manifest(), "file.ext", getTestXmlParser("page_cardcollection.xml"))) {
            val page = this
            with(getAnalyticsEvents(Trigger.VISIBLE)) {
                assertEquals(1, size)
                assertEquals("page", this[0].action)
            }
            assertEquals(2, cards.size)
            with(cards[0]) {
                assertEquals("${page.id}-0", id)
                assertTrue(getAnalyticsEvents(Trigger.VISIBLE).isEmpty())
                assertTrue(getAnalyticsEvents(Trigger.HIDDEN).isEmpty())
                assertEquals(page.cardBackgroundColor, backgroundColor)
                assertEquals(2, content.size)
                assertIs<Spacer>(content[0])
                assertIs<Paragraph>(content[1])
            }
            with(cards[1]) {
                assertEquals("card2", id)
                with(getAnalyticsEvents(Trigger.VISIBLE)) {
                    assertEquals(2, size)
                    assertEquals("card2", this[0].action)
                    assertEquals(Trigger.DEFAULT, this[0].trigger)
                    assertEquals("visible", this[1].action)
                    assertEquals(Trigger.VISIBLE, this[1].trigger)
                }
                with(getAnalyticsEvents(Trigger.HIDDEN)) {
                    assertEquals(1, size)
                    assertEquals("hidden", this[0].action)
                    assertEquals(Trigger.HIDDEN, this[0].trigger)
                }
                assertEquals(TestColors.GREEN, backgroundColor)
                assertTrue(content.isEmpty())
            }
        }
    }

    @Test
    fun testParseCardCollectionPageInvalidType() = runTest {
        assertFailsWith(XmlPullParserException::class) {
            CardCollectionPage(Manifest(), null, getTestXmlParser("page_invalid_type.xml"))
        }
    }
    // endregion Parse XML
}
