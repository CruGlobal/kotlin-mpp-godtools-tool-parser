package org.cru.godtools.shared.tool.analytics

import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.tract.TractPage
import kotlin.test.Test
import kotlin.test.assertEquals

class ToolAnalyticsScreenNamesTest {
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
}
