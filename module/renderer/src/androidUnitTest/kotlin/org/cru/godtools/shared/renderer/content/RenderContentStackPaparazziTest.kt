package org.cru.godtools.shared.renderer.content

import com.android.ide.common.rendering.api.SessionParams.RenderingMode
import io.fluidsonic.locale.Locale
import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Text

class RenderContentStackPaparazziTest : BasePaparazziTest(renderingMode = RenderingMode.SHRINK) {
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
    )
}
