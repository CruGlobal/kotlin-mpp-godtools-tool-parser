package org.cru.godtools.shared.renderer.content

import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Button
import org.cru.godtools.shared.tool.parser.model.Form
import org.cru.godtools.shared.tool.parser.model.Input
import org.cru.godtools.shared.tool.parser.model.Text

class RenderFormPaparazziTest : BasePaparazziTest() {
    private val state = State().apply {
        updateFormFieldValue("name", "John Doe")
        updateFormFieldValue("email", "john.doe@example.com")
    }

    @Test
    fun `RenderForm()`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Form(manifest) {
                    listOf(
                        Text(it, text = "Signup for follow up"),
                        Input(it, type = Input.Type.HIDDEN, name = "followup_id"),
                        Input(it, name = "name", label = { Text(it, text = "Name") }),
                        Input(it, name = "email", label = { Text(it, text = "Email") }),
                        Button(it, text = { Text(it, text = "Submit") }),
                    )
                },
            ),
            state = state
        )
    }
}
