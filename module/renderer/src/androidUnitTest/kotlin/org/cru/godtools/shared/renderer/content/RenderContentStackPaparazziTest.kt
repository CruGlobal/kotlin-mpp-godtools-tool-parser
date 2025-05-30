package org.cru.godtools.shared.renderer.content

import io.fluidsonic.locale.Locale
import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.tool.parser.model.Button
import org.cru.godtools.shared.tool.parser.model.Dimension
import org.cru.godtools.shared.tool.parser.model.Gravity
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Text

class RenderContentStackPaparazziTest : BasePaparazziTest() {
    @Test
    fun `RenderContentStack() - RTL`() {
        val manifest = Manifest(locale = Locale.forLanguageTag("ar"))
        contentSnapshot {
            RenderContentStack(layoutDirectionContent(manifest))
        }
    }

    @Test
    fun `RenderContentStack() - LTR`() {
        val manifest = Manifest(locale = Locale.forLanguageTag("en"))
        contentSnapshot {
            RenderContentStack(layoutDirectionContent(manifest))
        }
    }

    private fun layoutDirectionContent(manifest: Manifest) = listOf(
        Text(manifest, text = "Start", textAlign = Text.Align.START),
        Text(manifest, text = "End", textAlign = Text.Align.END),
        Button(
            manifest,
            text = { Text(it, "Start") },
            width = Dimension.Pixels(100),
            gravity = Gravity.Horizontal.START
        ),
        Button(manifest, text = { Text(it, "End") }, width = Dimension.Pixels(100), gravity = Gravity.Horizontal.END),
    )
}
