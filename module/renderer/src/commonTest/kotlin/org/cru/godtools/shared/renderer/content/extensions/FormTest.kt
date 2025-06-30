package org.cru.godtools.shared.renderer.content.extensions

import app.cash.turbine.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Form
import org.cru.godtools.shared.tool.parser.model.Input
import org.cru.godtools.shared.tool.parser.model.Paragraph

class FormTest {
    private val state = State().apply {
        updateFormFieldValue("name", "John")
        updateFormFieldValue("email", "john.doe@example.com")
    }

    @Test
    fun `submitForm - Simple`() = runTest {
        val form = Form {
            listOf(
                Input(it, name = "name"),
                Input(it, name = "email"),
            )
        }

        state.events.test {
            assertTrue(form.submitForm(state))
            assertEquals(
                State.Event.SubmitForm(mapOf("name" to "John", "email" to "john.doe@example.com")),
                awaitItem(),
            )
        }
    }

    @Test
    fun `submitForm - Nested`() = runTest {
        val form = Form {
            listOf(
                Input(it, name = "name"),
                Paragraph(it) {
                    listOf(
                        Input(it, name = "email"),
                    )
                },
            )
        }

        state.events.test {
            assertTrue(form.submitForm(state))
            assertEquals(
                State.Event.SubmitForm(mapOf("name" to "John", "email" to "john.doe@example.com")),
                awaitItem(),
            )
        }
    }

    @Test
    fun `submitForm - Ignore Inputs outside of form`() = runTest {
        val paragraph = Paragraph {
            listOf(
                Input(it, name = "name"),
                Form {
                    listOf(
                        Input(it, name = "email"),
                    )
                }
            )
        }
        val form = paragraph.content.filterIsInstance<Form>().first()

        state.events.test {
            assertTrue(form.submitForm(state))
            assertEquals(
                State.Event.SubmitForm(mapOf("email" to "john.doe@example.com")),
                awaitItem(),
            )
        }
    }

    @Test
    fun `submitForm - Hidden Inputs`() = runTest {
        val form = Form {
            listOf(
                Input(it, name = "hidden", type = Input.Type.HIDDEN, value = "value"),
                Input(it, name = "name"),
                Input(it, name = "email"),
            )
        }

        state.events.test {
            assertTrue(form.submitForm(state))
            assertEquals(
                State.Event.SubmitForm(mapOf("name" to "John", "email" to "john.doe@example.com", "hidden" to "value")),
                awaitItem(),
            )
        }
    }

    @Test
    fun `submitForm - Validation - Passed`() = runTest {
        val form = Form {
            listOf(
                Input(it, name = "name", isRequired = true),
                Input(it, name = "email", isRequired = true),
            )
        }

        assertFalse(state.isFormFieldValidationEnabled("name"))
        assertFalse(state.isFormFieldValidationEnabled("email"))

        state.events.test {
            assertTrue(form.submitForm(state))
            assertEquals(
                State.Event.SubmitForm(mapOf("name" to "John", "email" to "john.doe@example.com")),
                awaitItem(),
            )
        }

        assertFalse(state.isFormFieldValidationEnabled("name"))
        assertFalse(state.isFormFieldValidationEnabled("email"))
    }

    @Test
    fun `submitForm - Validation - Failed`() = runTest {
        val form = Form {
            listOf(
                Input(it, name = "name_missing", isRequired = true),
                Input(it, name = "email", isRequired = true),
            )
        }

        assertFalse(state.isFormFieldValidationEnabled("name_missing"))
        assertFalse(state.isFormFieldValidationEnabled("email"))

        state.events.test {
            assertFalse(form.submitForm(state))
            expectNoEvents()
        }

        assertTrue(state.isFormFieldValidationEnabled("name_missing"))
        assertTrue(state.isFormFieldValidationEnabled("email"))
    }
}
