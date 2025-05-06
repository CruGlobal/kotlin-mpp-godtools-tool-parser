package org.cru.godtools.shared.tool.parser.model.tract

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.Button
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Paragraph
import org.cru.godtools.shared.tool.parser.model.TRANSPARENT
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.WHITE
import org.cru.godtools.shared.tool.parser.model.toEventIds
import org.cru.godtools.shared.tool.parser.model.toPlatformColor

@RunOnAndroidWith(AndroidJUnit4::class)
class ModalTest : UsesResources("model/tract") {
    @Test
    fun testParseModal() = runTest {
        val modal = TractPage(Manifest(), "testPage", getTestXmlParser("modal.xml")).modals.single()
        assertEquals("testPage-0", modal.id)
        assertFixedAttributes(modal)
        assertEquals("listener1 listener2".toEventIds().toSet(), modal.listeners)
        assertEquals("dismiss-listener1 dismiss-listener2".toEventIds().toSet(), modal.dismissListeners)
        assertEquals(2, modal.content.size)
        assertIs<Paragraph>(modal.content[0])
        assertIs<Paragraph>(modal.content[1])
        assertEquals("Thank you", modal.title!!.text)
    }

    private fun assertFixedAttributes(modal: Modal) {
        assertEquals(TRANSPARENT.toPlatformColor(), modal.primaryColor)
        assertEquals(WHITE.toPlatformColor(), modal.primaryTextColor)

        assertEquals(WHITE.toPlatformColor(), modal.buttonColor)
        assertEquals(Button.Style.OUTLINED, modal.buttonStyle)

        assertEquals(Text.Align.CENTER, modal.textAlign)
        assertEquals(WHITE, modal.textColor)
    }
}
