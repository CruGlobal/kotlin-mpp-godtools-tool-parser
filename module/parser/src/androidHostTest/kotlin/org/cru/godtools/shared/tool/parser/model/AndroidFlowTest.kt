package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals
import org.cru.godtools.shared.tool.parser.model.Flow.Companion.DEFAULT_ITEM_WIDTH
import org.cru.godtools.shared.tool.parser.model.Flow.Companion.DEFAULT_ROW_GRAVITY

class AndroidFlowTest {
    @Test
    fun testRowGravity() {
        with(null as Flow?) {
            assertEquals(DEFAULT_ROW_GRAVITY, rowGravity)
        }
    }

    @Test
    fun testItemWidth() {
        with(null as Flow.Item?) {
            assertEquals(DEFAULT_ITEM_WIDTH, width)
        }
    }
}
