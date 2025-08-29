package org.cru.godtools.shared.renderer.lesson

import com.github.ajalt.colormath.model.RGB
import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Spacer
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.lesson.LessonPage

class RenderLessonPagePaparazziTest : BasePaparazziTest() {
    @Test
    fun `RenderLessonPage() - Background - Layers`() = contentSnapshot {
        RenderLessonPage(
            LessonPage(
                manifest,
                backgroundColor = RGB(1, 0, 0),
                backgroundImage = "black_panther",
                controlColor = RGB(0, 0, 0),
            ) {
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
            }
        )
    }

    @Test
    fun `RenderLessonPage() - Content - Spacers`() = contentSnapshot {
        RenderLessonPage(
            LessonPage(manifest) {
                listOf(
                    Text(it, text = "Top"),
                    Spacer(it),
                    Text(it, text = "Centered"),
                    Spacer(it),
                    Text(it, text = "Bottom"),
                )
            }
        )
    }

    @Test
    fun `RenderLessonPage() - Content - Scrollable`() = contentSnapshot {
        RenderLessonPage(
            LessonPage(manifest) { page ->
                List(50) { i -> Text(page, text = "Line ${i + 1}") }
            }
        )
    }

    @Test
    fun `RenderLessonPage() - Navigation - Pages`() {
        val manifest = Manifest(
            pages = {
                listOf(
                    LessonPage(it) { listOf(Text(it, text = "First Page")) },
                    LessonPage(it) { listOf(Text(it, text = "Second Page")) },
                    LessonPage(it) { listOf(Text(it, text = "Third Page")) },
                )
            }
        )

        manifest.pages.forEachIndexed { i, page ->
            contentSnapshot("page_${i + 1}") {
                RenderLessonPage(page as LessonPage)
            }
        }
    }
}
