package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.internal.runBlockingTest
import org.cru.godtools.tool.model.tips.InlineTip
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@RunOnAndroidWith(AndroidJUnit4::class)
class FlowTest : UsesResources() {
    @Test
    fun testParseFlow() = runBlockingTest {
        val flow = Flow(Manifest(), getTestXmlParser("flow.xml"))
        assertEquals(4, flow.items.size)
        assertIs<Spacer>(flow.items[0].content.single())
        with(flow.items[1]) {
            assertEquals(2, content.size)
            assertIs<Text>(content[0])
            assertIs<Spacer>(content[1])
        }
        assertIs<Paragraph>(flow.items[2].content.single())
        assertIs<InlineTip>(flow.items[3].content.single())
    }
}
