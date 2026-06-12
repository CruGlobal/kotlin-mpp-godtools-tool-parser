package org.cru.godtools.shared.renderer.content

import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.tool.parser.model.Dimension
import org.cru.godtools.shared.tool.parser.model.Gravity
import org.cru.godtools.shared.tool.parser.model.Image

class RenderImagePaparazziTest : BasePaparazziTest() {
    @Test
    fun `RenderImage() - Simple`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Image(manifest, resource = "bruce")
            )
        )
    }

    @Test
    fun `RenderImage() - Width & Gravity`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Image(
                    manifest,
                    resource = "bruce",
                    width = Dimension.Pixels(100),
                    gravity = Gravity.Horizontal.START
                ),
                Image(
                    manifest,
                    resource = "bruce",
                    width = Dimension.Percent(0.5f),
                    gravity = Gravity.Horizontal.CENTER,
                ),
                Image(
                    manifest,
                    resource = "bruce",
                    width = Dimension.Pixels(100),
                    gravity = Gravity.Horizontal.END
                ),
            ),
        )
    }
}
