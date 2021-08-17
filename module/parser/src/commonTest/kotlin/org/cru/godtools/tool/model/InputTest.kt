package org.cru.godtools.tool.model

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.internal.runBlockingTest
import org.cru.godtools.tool.model.Input.Type.Companion.toTypeOrNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunOnAndroidWith(AndroidJUnit4::class)
class InputTest : UsesResources() {
    @Test
    fun testParseInputHidden() = runBlockingTest {
        val input = Input(Manifest(), getTestXmlParser("input_hidden.xml"))
        assertEquals(Input.Type.HIDDEN, input.type)
        assertEquals("destination_id", input.name)
        assertEquals("1", input.value)
        assertFalse(input.isRequired)

        // test validateValue
        assertNull(input.validateValue(null))
        assertNull(input.validateValue(""))
        assertNull(input.validateValue("     "))
    }

    @Test
    fun testParseInputText() = runBlockingTest {
        val input = Input(Manifest(), getTestXmlParser("input_text.xml"))
        assertEquals(Input.Type.TEXT, input.type)
        assertEquals("name", input.name)
        assertEquals("Name", input.label!!.text)
        assertEquals("First Name and Last Name", input.placeholder!!.text)
        assertTrue(input.isRequired)

        // test validateValue
        assertEquals(Input.Error.Required, input.validateValue(null))
        assertEquals(Input.Error.Required, input.validateValue(""))
        assertEquals(Input.Error.Required, input.validateValue("     "))
        assertNull(input.validateValue(" a "))
    }

    @Test
    fun testParseInputEmail() = runBlockingTest {
        val input = Input(Manifest(), getTestXmlParser("input_email.xml"))
        assertEquals(Input.Type.EMAIL, input.type)
        assertEquals("email", input.name)
        assertTrue(input.isRequired)

        // test validateValue
        assertEquals(Input.Error.Required, input.validateValue(null))
        assertEquals(Input.Error.Required, input.validateValue(""))
        assertEquals(Input.Error.Required, input.validateValue("     "))
        assertEquals(Input.Error.InvalidEmail, input.validateValue("a"))
        assertNull(input.validateValue("a@example.com"))
    }

    @Test
    fun testParseInputType() {
        assertEquals(Input.Type.EMAIL, "email".toTypeOrNull())
        assertEquals(Input.Type.HIDDEN, "hidden".toTypeOrNull())
        assertEquals(Input.Type.PHONE, "phone".toTypeOrNull())
        assertEquals(Input.Type.TEXT, "text".toTypeOrNull())
        assertNull("awherhjasdf".toTypeOrNull())
    }
}
