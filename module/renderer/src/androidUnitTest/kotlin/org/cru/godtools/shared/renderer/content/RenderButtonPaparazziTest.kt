package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import com.github.ajalt.colormath.model.RGB
import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Button
import org.cru.godtools.shared.tool.parser.model.Dimension
import org.cru.godtools.shared.tool.parser.model.Gravity
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Text

class RenderButtonPaparazziTest : BasePaparazziTest() {
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

    @Test
    fun `RenderButton() - Width`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Button(
                    style = Button.Style.CONTAINED,
                    width = Dimension.Pixels(200),
                    text = { Text(it, "200dp Width") }
                ),
                Button(
                    style = Button.Style.CONTAINED,
                    width = Dimension.Percent(0.5f),
                    text = { Text(it, "50% Width") }
                ),
                Button(
                    style = Button.Style.CONTAINED,
                    width = Dimension.Percent(1f),
                    text = { Text(it, "100% Width") }
                ),
            )
        )
    }

    @Test
    fun `RenderButton() - Icon`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Button(
                    manifest,
                    style = Button.Style.CONTAINED,
                    iconName = "black_panther",
                    iconGravity = Gravity.Horizontal.START,
                    text = { Text(it, "Start Icon") }
                ),
                Button(
                    manifest,
                    style = Button.Style.CONTAINED,
                    iconName = "black_panther",
                    iconGravity = Gravity.Horizontal.END,
                    text = { Text(it, "End Icon") }
                ),
                Button(
                    manifest,
                    style = Button.Style.CONTAINED,
                    iconName = "black_panther",
                    iconGravity = Gravity.Horizontal.START,
                    iconSize = 48,
                    text = { Text(it, "Large Icon") }
                ),
                Button(
                    manifest,
                    style = Button.Style.CONTAINED,
                    iconName = "bruce",
                    iconGravity = Gravity.Horizontal.START,
                    iconSize = 48,
                    text = { Text(it, "Large Icon") }
                ),
            )
        )
    }

    @Test
    fun `RenderButton() - Gravity`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Button(
                    style = Button.Style.CONTAINED,
                    width = Dimension.Percent(0.5f),
                    gravity = Gravity.Horizontal.START,
                    text = { Text(it, "Start Gravity") }
                ),
                Button(
                    style = Button.Style.CONTAINED,
                    width = Dimension.Percent(0.5f),
                    gravity = Gravity.Horizontal.CENTER,
                    text = { Text(it, "Center Gravity") }
                ),
                Button(
                    style = Button.Style.CONTAINED,
                    width = Dimension.Percent(0.5f),
                    gravity = Gravity.Horizontal.END,
                    text = { Text(it, "End Gravity") }
                ),
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }

    @Test
    fun `RenderButton() - Large Text`() {
        val manifest = Manifest(textScale = 3.0)

        contentSnapshot {
            RenderContentStack(
                listOf(
                    Button(
                        parent = manifest,
                        style = Button.Style.CONTAINED,
                        text = { Text(it, "Large Text") }
                    ),
                    Button(
                        parent = manifest,
                        style = Button.Style.CONTAINED,
                        text = { Text(it, "Large Text") }
                    ),
                    Button(
                        parent = manifest,
                        style = Button.Style.OUTLINED,
                        text = { Text(it, "Large Text") }
                    ),
                    Button(
                        parent = manifest,
                        style = Button.Style.OUTLINED,
                        text = { Text(it, "Large Text") }
                    ),
                )
            )
        }
    }

    @Test
    fun `RenderButton() - IsInvisible`() {
        val state = State()
        state.setVar("a", listOf("value"))

        contentSnapshot {
            RenderContentStack(
                listOf(
                    Text(text = "Before"),
                    Button(
                        style = Button.Style.CONTAINED,
                        width = Dimension.Percent(0.5f),
                        gravity = Gravity.Horizontal.CENTER,
                        text = { Text(it, "Invisible Button") },
                        invisibleIf = "isSet(a)"
                    ),
                    Text(text = "After")
                ),
                modifier = Modifier.fillMaxWidth(),
                state = state
            )
        }
    }
}
