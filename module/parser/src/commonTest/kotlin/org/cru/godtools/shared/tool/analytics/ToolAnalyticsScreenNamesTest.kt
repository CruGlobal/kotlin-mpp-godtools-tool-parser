package org.cru.godtools.shared.tool.analytics

import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.lesson.LessonPage
import org.cru.godtools.shared.tool.parser.model.page.CardCollectionPage
import org.cru.godtools.shared.tool.parser.model.page.ContentPage
import org.cru.godtools.shared.tool.parser.model.tips.Tip
import org.cru.godtools.shared.tool.parser.model.tips.TipPage
import org.cru.godtools.shared.tool.parser.model.tract.TractPage
import kotlin.test.Test
import kotlin.test.assertEquals

class ToolAnalyticsScreenNamesTest {
    @Test
    fun testForCyoaPage() {
        val page = ContentPage(
            manifest = Manifest(code = "cyoa"),
            id = "page"
        )

        assertEquals("cyoa:page", ToolAnalyticsScreenNames.forCyoaPage(page))
    }

    @Test
    fun testForCyoaCardCollectionCard() {
        val card = CardCollectionPage.Card(
            page = CardCollectionPage(
                manifest = Manifest(code = "cyoa"),
                id = "page"
            ),
            id = "card"
        )

        assertEquals("cyoa:page:card", ToolAnalyticsScreenNames.forCyoaCardCollectionCard(card))
    }

    @Test
    fun testForLessonPage() {
        val manifest = Manifest(
            code = "lessonhs",
            pages = { listOf(LessonPage(it), LessonPage(it)) }
        )

        assertEquals("lessonhs-0", ToolAnalyticsScreenNames.forLessonPage(manifest.pages[0] as LessonPage))
        assertEquals("lessonhs-1", ToolAnalyticsScreenNames.forLessonPage(manifest.pages[1] as LessonPage))
    }

    @Test
    fun testForTractPage() {
        val manifest = Manifest(
            code = "tool",
            pages = { listOf(TractPage(it)) }
        )
        val page = manifest.pages.filterIsInstance<TractPage>().first()

        assertEquals("tool-0", ToolAnalyticsScreenNames.forTractPage(page))
        assertEquals("tool-0a", ToolAnalyticsScreenNames.forTractPage(page, TractPage.Card(position = 0)))
        assertEquals("tool-0b", ToolAnalyticsScreenNames.forTractPage(page, TractPage.Card(position = 1)))
        assertEquals("tool-0z", ToolAnalyticsScreenNames.forTractPage(page, TractPage.Card(position = 25)))
        assertEquals("tool-0-26", ToolAnalyticsScreenNames.forTractPage(page, TractPage.Card(position = 26)))
        assertEquals("tool-0-100", ToolAnalyticsScreenNames.forTractPage(page, TractPage.Card(position = 100)))
    }

    @Test
    fun testForTipPage() {
        val tip = Tip(Manifest(code = "tool"), id = "tipId")

        assertEquals("tool-tip-tipId-0", ToolAnalyticsScreenNames.forTipPage(TipPage(tip, position = 0)))
        assertEquals("tool-tip-tipId-0", ToolAnalyticsScreenNames.forTipPage("tool", "tipId", 0))
        assertEquals("tool-tip-tipId-1", ToolAnalyticsScreenNames.forTipPage(TipPage(tip, position = 1)))
    }
}
