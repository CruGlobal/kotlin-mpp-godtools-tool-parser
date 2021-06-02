package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@RunOnAndroidWith(AndroidJUnit4::class)
class AccordionTest : UsesResources() {
    @Test
    fun testParseAccordion() {
        val accordion = Accordion(Manifest(), getTestXmlParser("accordion.xml"))
        assertEquals(2, accordion.sections.size)

        with(accordion.sections[0]) {
            assertEquals("Section 1", header!!.text)
            assertEquals(1, content.size)
            assertIs<Text>(content[0])
        }
    }
}
