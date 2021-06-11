package org.cru.godtools.tool.model.tract

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.Paragraph
import org.cru.godtools.tool.model.toEventIds
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@RunOnAndroidWith(AndroidJUnit4::class)
class ModalTest : UsesResources("model/tract") {
    @Test
    fun testParseModal() {
        val modal = TractPage(Manifest(), null, getTestXmlParser("modal.xml")).modals.single()
        assertEquals("listener1 listener2".toEventIds().toSet(), modal.listeners)
        assertEquals("dismiss-listener1 dismiss-listener2".toEventIds().toSet(), modal.dismissListeners)
        assertEquals(2, modal.content.size)
        assertIs<Paragraph>(modal.content[0])
        assertIs<Paragraph>(modal.content[1])
        assertEquals("Thank you", modal.title!!.text)
    }
}
