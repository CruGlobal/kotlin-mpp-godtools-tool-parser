package org.cru.godtools.tool.model.tract

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.model.Button
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.Paragraph
import org.cru.godtools.tool.model.TEXT_SIZE_BASE
import org.cru.godtools.tool.model.TEXT_SIZE_MODAL
import org.cru.godtools.tool.model.TEXT_SIZE_MODAL_TITLE
import org.cru.godtools.tool.model.TRANSPARENT
import org.cru.godtools.tool.model.Text
import org.cru.godtools.tool.model.WHITE
import org.cru.godtools.tool.model.toEventIds
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@RunOnAndroidWith(AndroidJUnit4::class)
class ModalTest : UsesResources("model/tract") {
    @Test
    fun testParseModal() {
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

    @Test
    fun testTextScale() {
        with(Modal(title = { Text(it) })) {
            assertEquals(TEXT_SIZE_MODAL, (TEXT_SIZE_BASE * textScale).toInt())
            assertEquals(TEXT_SIZE_MODAL_TITLE, (TEXT_SIZE_BASE * title!!.textScale).toInt())
        }

        with(Modal(TractPage(textScale = 2.0), title = { Text(it) })) {
            assertEquals(2 * TEXT_SIZE_MODAL, (TEXT_SIZE_BASE * textScale).toInt())
            assertEquals(2 * TEXT_SIZE_MODAL_TITLE, (TEXT_SIZE_BASE * title!!.textScale).toInt())
        }

        with(Modal(title = { Text(it, textScale = 2.0) })) {
            assertEquals(TEXT_SIZE_MODAL, (TEXT_SIZE_BASE * textScale).toInt())
            assertEquals(2 * TEXT_SIZE_MODAL_TITLE, (TEXT_SIZE_BASE * title!!.textScale).toInt())
        }
    }

    private fun assertFixedAttributes(modal: Modal) {
        assertEquals(TRANSPARENT, modal.primaryColor)
        assertEquals(WHITE, modal.primaryTextColor)

        assertEquals(WHITE, modal.buttonColor)
        assertEquals(Button.Style.OUTLINED, modal.buttonStyle)

        assertEquals(Text.Align.CENTER, modal.textAlign)
        assertEquals(WHITE, modal.textColor)
    }
}
