package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.internal.runBlockingTest
import org.cru.godtools.tool.model.Flow.Companion.DEFAULT_ITEM_WIDTH
import org.cru.godtools.tool.model.Flow.Companion.DEFAULT_ROW_GRAVITY
import org.cru.godtools.tool.model.tips.InlineTip
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
class FlowTest : UsesResources() {
    @Test
    fun testParseFlowDefaults() = runBlockingTest {
        val flow = Flow(Manifest(), getTestXmlParser("flow_defaults.xml"))
        assertEquals(DEFAULT_ITEM_WIDTH, flow.itemWidth)
        assertEquals(DEFAULT_ROW_GRAVITY, flow.rowGravity)
        assertTrue(flow.items.isEmpty())
    }

    @Test
    fun testParseFlow() = runBlockingTest {
        val flow = Flow(Manifest(), getTestXmlParser("flow.xml"))
        assertEquals(0.33f, assertIs<Dimension.Percent>(flow.itemWidth).value, DIMENSION_TOLERANCE)
        assertEquals(Gravity.Horizontal.END, flow.rowGravity)
        assertEquals(4, flow.items.size)
        assertIs<Spacer>(flow.items[0].content.single())
        with(flow.items[1]) {
            assertEquals(25, assertIs<Dimension.Pixels>(width).value)
            assertEquals(2, content.size)
            assertIs<Text>(content[0])
            assertIs<Spacer>(content[1])
        }
        assertIs<Paragraph>(flow.items[2].content.single())
        assertIs<InlineTip>(flow.items[3].content.single())
    }

    @Test
    fun testRowGravity() {
        with(null as Flow?) {
            assertEquals(DEFAULT_ROW_GRAVITY, rowGravity)
        }
    }
}
