package org.cru.godtools.shared.tool.parser.model.page

import kotlin.test.Test
import kotlin.test.assertEquals
import org.cru.godtools.shared.tool.parser.model.TestColors

class AndroidPageTest {
    @Test
    fun testNullablePageControlColor() {
        var page: Page? = null
        assertEquals(DEFAULT_CONTROL_COLOR, page.controlColor)

        page = PageTest.TestPage(controlColor = TestColors.GREEN)
        assertEquals(TestColors.GREEN, page.controlColor)
    }
}
