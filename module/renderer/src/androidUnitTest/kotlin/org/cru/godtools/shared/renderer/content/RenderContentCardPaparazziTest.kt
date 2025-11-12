package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import com.github.ajalt.colormath.model.RGB
import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Card
import org.cru.godtools.shared.tool.parser.model.Text

class RenderContentCardPaparazziTest : BasePaparazziTest() {

    @Test
    fun `RenderContentCard() - Background Color`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Card(
                    backgroundColor = RGB("#dadada"),
                    content = listOf(
                        Text(text = "Text 1"),
                        Text(text = "Text 2"),
                        Text(text = "Text 3"),
                        Text(text = "Text 4"),
                        Text(text = "Text 5"),
                        Text(text = "Text 6")
                    )
                ),
                Card(
                    backgroundColor = RGB("#c0c0c0"),
                    content = listOf(
                        Text(text = "Text 1"),
                        Text(text = "Text 2"),
                        Text(text = "Text 3"),
                        Text(text = "Text 4"),
                        Text(text = "Text 5"),
                        Text(text = "Text 6")
                    )
                ),
                Card(
                    backgroundColor = RGB("#a7a7a7"),
                    content = listOf(
                        Text(text = "Text 1"),
                        Text(text = "Text 2"),
                        Text(text = "Text 3"),
                        Text(text = "Text 4"),
                        Text(text = "Text 5"),
                        Text(text = "Text 6")
                    )
                )
            )
        )
    }

    @Test
    fun `RenderContentCard() - IsInvisible`() {
        val state: State = State()
        state.setVar("a", listOf("value"))

        contentSnapshot {
            RenderContentStack(
                listOf(
                    Text(text = "Before"),
                    Card(
                        backgroundColor = RGB("#a7a7a7"),
                        invisibleIf = "isSet(a)",
                        content = listOf(
                            Text(text = "Card", textAlign = Text.Align.CENTER),
                            Text(text = "Should", textAlign = Text.Align.CENTER),
                            Text(text = "Be", textAlign = Text.Align.CENTER),
                            Text(text = "Invisible", textAlign = Text.Align.CENTER)
                        )
                    ),
                    Text(text = "After")
                ),
                modifier = Modifier.fillMaxWidth(),
                state = state
            )
        }
    }
}
