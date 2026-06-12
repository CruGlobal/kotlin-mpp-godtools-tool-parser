package org.cru.godtools.shared.renderer.tract

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.github.ajalt.colormath.model.RGB
import com.github.ajalt.colormath.model.RGB.Companion.invoke
import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.renderer.lesson.RenderLessonPage
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Button
import org.cru.godtools.shared.tool.parser.model.Paragraph
import org.cru.godtools.shared.tool.parser.model.Spacer
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.lesson.LessonPage
import org.cru.godtools.shared.tool.parser.model.tract.Modal

class RenderTractModalPaparazziTest : BasePaparazziTest() {
    private val modal = Modal(title = { Text(it, text = "Thank you") }) {
        listOf(
            Paragraph(it) {
                listOf(
                    Text(it, text = "Check your email soon for your first study in following Jesus Christ."),
                )
            },
            Paragraph(it) {
                listOf(
                    Text(it, text = "If you do not receive it, please check your spam folder."),
                )
            },
            Paragraph(it) {
                listOf(
                    Button(it) { Text(it, text = "Close") },
                )
            },
            Paragraph(it) {
                listOf(
                    Text(
                        it,
                        text = "If this sign up occurs offline, you will need to reopen the app " +
                            "while on Wifi to have the signup automatically submit.",
                        textScale = 0.75,
                    ),
                )
            },
        )
    }
    private val state = State()

    @Test
    fun `RenderTractModal() - Default`() = contentSnapshot {
        // TODO: replace with a tract page once we have a composable for one
        RenderLessonPage(
            LessonPage(manifest, backgroundImage = "black_panther") {
                listOf(
                    Spacer(it),
                    Text(
                        it,
                        text = "Background Layers",
                        textAlign = Text.Align.CENTER,
                        textColor = RGB(0, 0, 1),
                    ),
                    Spacer(it),
                )
            },
        )

        RenderTractModal(modal, state, modifier = Modifier.fillMaxSize())
    }
}
