package org.cru.godtools.shared.renderer.content

import com.github.ajalt.colormath.model.RGB
import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.tool.parser.model.Text

class RenderTextPaparazziTest : BasePaparazziTest() {
    @Test
    fun `RenderText() - Simple`() = contentSnapshot {
        RenderContentStack(listOf(Text(text = "Simple Text")))
    }

    @Test
    fun `RenderText() - Color`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Text(text = "Default Color"),
                Text(text = "Red Color", textColor = RGB(1, 0, 0, 1)),
                Text(text = "Green Color", textColor = RGB(0, 1, 0, 1)),
                Text(text = "Blue Color", textColor = RGB(0, 0, 1, 1)),
            ),
        )
    }

    @Test
    fun `RenderText() - Styles`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Text(text = "Italic Text", textStyles = setOf(Text.Style.ITALIC)),
                Text(text = "Underline Text", textStyles = setOf(Text.Style.UNDERLINE)),
                Text(text = "Italic Underline Text", textStyles = setOf(Text.Style.ITALIC, Text.Style.UNDERLINE)),
            ),
        )
    }

    @Test
    fun `RenderText() - Font Weight`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Text(text = "Font Weight 100", fontWeight = 100),
                Text(text = "Font Weight 200", fontWeight = 200),
                Text(text = "Font Weight 300", fontWeight = 300),
                Text(text = "Font Weight 400", fontWeight = 400),
                Text(text = "Font Weight 500", fontWeight = 500),
                Text(text = "Font Weight 600", fontWeight = 600),
                Text(text = "Font Weight 700", fontWeight = 700),
                Text(text = "Font Weight 800", fontWeight = 800),
                Text(text = "Font Weight 900", fontWeight = 900),
                Text(text = "Font Weight 1000", fontWeight = 1000),
            ),
        )
    }

    @Test
    fun `RenderText() - Text Align`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Text(text = "Start Align", textAlign = Text.Align.START),
                Text(text = "Center Align", textAlign = Text.Align.CENTER),
                Text(text = "End Align", textAlign = Text.Align.END),
            ),
        )
    }

    @Test
    fun `RenderText() - With Start Image`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Text(
                    manifest,
                    text = "Start Image",
                    textAlign = Text.Align.START,
                    startImage = "bruce"
                )
            ),
        )
    }

    @Test
    fun `RenderText() - With Start And End Image`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Text(
                    manifest,
                    text = "Start And End Image",
                    textAlign = Text.Align.START,
                    startImage = "bruce",
                    endImage = "bruce"
                )
            ),
        )
    }

    @Test
    fun `RenderText() - With End Image`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Text(
                    manifest,
                    text = "End Image",
                    textAlign = Text.Align.START,
                    endImage = "bruce"
                )
            ),
        )
    }

    @Test
    fun `RenderText() - Multiline Text With Start And End Image`() = contentSnapshot {
        val multilineString = """
            |Lorem ipsum dolor sit amet, consectetur adipiscing elit, 
            |sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. 
            |Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. 
            |Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. 
            |Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
            """
            .trimMargin()
            .replace("\n", "")

        RenderContentStack(
            listOf(
                Text(
                    manifest,
                    text = multilineString,
                    textAlign = Text.Align.START,
                    startImage = "bruce",
                    endImage = "bruce"
                )
            ),
        )
    }

    @Test
    fun `RenderText() - Tall Start And End Image`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Text(
                    manifest,
                    text = "Tall",
                    textAlign = Text.Align.START,
                    startImage = "tall",
                    endImage = "tall"
                )
            ),
        )
    }

    @Test
    fun `RenderText() - Wide Start And End Image`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Text(
                    manifest,
                    text = "Wide",
                    textAlign = Text.Align.START,
                    startImage = "wide",
                    endImage = "wide"
                )
            ),
        )
    }
}
