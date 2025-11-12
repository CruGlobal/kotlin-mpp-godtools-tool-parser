package org.cru.godtools.shared.tool.parser.model.page

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
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
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.page.CardCollectionPage.Header.Companion.DEFAULT_TEXT_ALIGN
import org.cru.godtools.shared.tool.parser.xml.XmlPullParserException

@RunOnAndroidWith(AndroidJUnit4::class)
class CardCollectionPageTest : UsesResources("model/page") {
    // region Parse XML
    @Test
    fun `CardCollectionPage Parsing`() = runTest {
        with(CardCollectionPage(Manifest(), "file.ext", getTestXmlParser("page_cardcollection.xml"))) {
            val page = this
            with(getAnalyticsEvents(Trigger.VISIBLE)) {
                assertEquals(1, size)
                assertEquals("page", this[0].action)
            }

            assertNotNull(page.header) { header ->
                assertEquals(2, header.content.size)
                assertEquals("Header", assertIs<Text>(header.content[0]).text)
                assertIs<Spacer>(header.content[1])
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
                    assertTrue(this[0].isTriggerType(Trigger.DEFAULT))
                    assertEquals("visible", this[1].action)
                    assertTrue(this[1].isTriggerType(Trigger.VISIBLE))
                }
                with(getAnalyticsEvents(Trigger.HIDDEN)) {
                    assertEquals(1, size)
                    assertEquals("hidden", this[0].action)
                    assertTrue(this[0].isTriggerType(Trigger.HIDDEN))
                }
                assertEquals(TestColors.GREEN, backgroundColor)
                assertTrue(content.isEmpty())
            }
        }
    }

    @Test
    fun `CardCollectionPage Parsing - Defaults`() = runTest {
        assertNotNull(CardCollectionPage(Manifest(), "f.ext", getTestXmlParser("page_cardcollection_defaults.xml"))) {
            assertTrue(it.analyticsEvents.isEmpty())
            assertNull(it.header)
            assertTrue(it.cards.isEmpty())
        }
    }

    @Test
    fun `CardCollectionPage Parsing - Invalid Page Type`() = runTest {
        assertFailsWith(XmlPullParserException::class) {
            CardCollectionPage(Manifest(), null, getTestXmlParser("page_invalid_type.xml"))
        }
    }
    // endregion Parse XML

    @Test
    fun `Property - children`() {
        val page = CardCollectionPage(header = { CardCollectionPage.Header(it) }) {
            listOf(
                CardCollectionPage.Card(it) { listOf(Text(it, "text")) },
                CardCollectionPage.Card(it) { listOf(Text(it, "text")) },
            )
        }

        assertEquals(3, page.children.size)
        assertEquals(listOf(page.header!!) + page.cards, page.children)
    }

    @Test
    fun `Property - children - No Header`() {
        val page = CardCollectionPage {
            listOf(
                CardCollectionPage.Card(it) { listOf(Text(it, "text")) },
                CardCollectionPage.Card(it) { listOf(Text(it, "text")) },
            )
        }

        assertEquals(2, page.children.size)
        assertEquals(page.cards, page.children)
    }

    // region Header
    @Test
    fun `Property - header - Styles - textAlign`() = runTest {
        val header = CardCollectionPage.Header(
            content = {
                listOf(
                    Text(it),
                    Text(it, textAlign = Text.Align.START),
                    Text(it, textAlign = Text.Align.CENTER),
                    Text(it, textAlign = Text.Align.END),
                )
            },
        )

        assertEquals(DEFAULT_TEXT_ALIGN, assertIs<Text>(header.content[0]).textAlign)
        assertEquals(Text.Align.START, assertIs<Text>(header.content[1]).textAlign)
        assertEquals(Text.Align.CENTER, assertIs<Text>(header.content[2]).textAlign)
        assertEquals(Text.Align.END, assertIs<Text>(header.content[3]).textAlign)
    }
    // endregion Header
}
