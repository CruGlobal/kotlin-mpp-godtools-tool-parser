package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.internal.runBlockingTest
import org.cru.godtools.tool.model.tips.InlineTip
import org.cru.godtools.tool.model.tips.Tip
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@RunOnAndroidWith(AndroidJUnit4::class)
class AccordionTest : UsesResources() {
    @Test
    fun testParseAccordion() = runBlockingTest {
        val accordion = Accordion(Manifest(), getTestXmlParser("accordion.xml"))
        assertEquals(2, accordion.sections.size)

        with(accordion.sections[0]) {
            assertEquals("Section 1", header!!.text)
            assertEquals(1, content.size)
            assertIs<Text>(content[0])
        }

        with(accordion.sections[1]) {
            assertEquals("Section 2", header!!.text)
            assertEquals(1, content.size)
            assertIs<Image>(content[0])
        }
    }

    @Test
    fun testTipsProperty() {
        val manifest = Manifest(tips = { listOf(Tip(it, "tip1"), Tip(it, "tip2")) })
        val accordion = Accordion(manifest) {
            listOf(
                Accordion.Section(it) { listOf(InlineTip(it, "tip1")) },
                Accordion.Section(it) { listOf(InlineTip(it, "tip2")) },
            )
        }
        assertEquals(listOf(manifest.findTip("tip1")!!, manifest.findTip("tip2")!!), accordion.tips)
    }
}
