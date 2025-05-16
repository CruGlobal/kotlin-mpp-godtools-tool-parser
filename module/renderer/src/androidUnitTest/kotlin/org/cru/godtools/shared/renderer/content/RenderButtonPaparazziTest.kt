package org.cru.godtools.shared.renderer.content

import com.android.ide.common.rendering.api.SessionParams.RenderingMode
import com.github.ajalt.colormath.model.RGB
import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.tool.parser.model.Button
import org.cru.godtools.shared.tool.parser.model.Text

class RenderButtonPaparazziTest : BasePaparazziTest(renderingMode = RenderingMode.SHRINK) {
    @Test
    fun `RenderButton() - Defaults`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Button(
                    style = Button.Style.CONTAINED,
                    text = { Text(it, "Default Contained Button") }
                ),
                Button(
                    style = Button.Style.OUTLINED,
                    text = { Text(it, "Default Outlined Button") }
                ),
            ),
        )
    }

    @Test
    fun `RenderButton() - Color`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Button(
                    style = Button.Style.CONTAINED,
                    color = RGB(1, 0, 0, 1),
                    text = { Text(it, "Red Contained Button") }
                ),
                Button(
                    style = Button.Style.OUTLINED,
                    color = RGB(1, 0, 0, 1),
                    text = { Text(it, "Red Outlined Button") }
                ),
                Button(
                    style = Button.Style.CONTAINED,
                    color = RGB(0, 1, 0, 1),
                    text = { Text(it, "Green Contained Button") }
                ),
                Button(
                    style = Button.Style.OUTLINED,
                    color = RGB(0, 1, 0, 1),
                    text = { Text(it, "Green Outlined Button") }
                ),
                Button(
                    style = Button.Style.CONTAINED,
                    color = RGB(0, 0, 1, 1),
                    text = { Text(it, "Blue Contained Button") }
                ),
                Button(
                    style = Button.Style.OUTLINED,
                    color = RGB(0, 0, 1, 1),
                    text = { Text(it, "Blue Outlined Button") }
                ),
            )
        )
    }
}
