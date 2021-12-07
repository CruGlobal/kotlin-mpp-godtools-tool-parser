package org.cru.godtools.tool.model.page

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.internal.runBlockingTest
import org.cru.godtools.tool.model.AnalyticsEvent.Trigger
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.Paragraph
import org.cru.godtools.tool.model.Spacer
import org.cru.godtools.tool.model.TestColors
import org.cru.godtools.tool.xml.XmlPullParserException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
class CardCollectionPageTest : UsesResources("model/page") {
    // region Parse XML
    @Test
    fun testParseCardCollectionPage() = runBlockingTest {
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
                assertEquals(page.cardBackgroundColor, backgroundColor)
                assertEquals(2, content.size)
                assertIs<Spacer>(content[0])
                assertIs<Paragraph>(content[1])
            }
            with(cards[1]) {
                assertEquals("card2", id)
                with(getAnalyticsEvents(Trigger.VISIBLE)) {
                    assertEquals(1, size)
                    assertEquals("card2", this[0].action)
                }
                assertTrue(getAnalyticsEvents(Trigger.HIDDEN).isEmpty())
                assertEquals(TestColors.GREEN, backgroundColor)
                assertTrue(content.isEmpty())
            }
        }
    }

    @Test
    fun testParseCardCollectionPageInvalidType() = runBlockingTest {
        assertFailsWith(XmlPullParserException::class) {
            CardCollectionPage(Manifest(), null, getTestXmlParser("page_invalid_type.xml"))
        }
    }
    // endregion Parse XML
}
