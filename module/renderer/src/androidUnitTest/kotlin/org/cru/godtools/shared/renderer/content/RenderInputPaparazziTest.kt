package org.cru.godtools.shared.renderer.content

import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Input
import org.cru.godtools.shared.tool.parser.model.Text

class RenderInputPaparazziTest : BasePaparazziTest() {
    private val state = State().apply {
        updateFormFieldValue("field", "value")
    }

    @Test
    fun `RenderInput() - Simple`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Input(label = { Text(it, text = "Label") }),
                Input(placeholder = { Text(it, text = "Placeholder") }),
                Input(label = { Text(it, text = "Label") }, placeholder = { Text(it, text = "Placeholder") })
            )
        )
    }

    @Test
    fun `RenderInput() - Has Value`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Input(label = { Text(it, text = "Label") }),
                Input(name = "field", label = { Text(it, text = "Label") }),
            ),
            state = state
        )
    }

    @Test
    fun `RenderInput() - Hidden`() = contentSnapshot {
        RenderContentStack(listOf(Input(type = Input.Type.HIDDEN)))
    }
}
