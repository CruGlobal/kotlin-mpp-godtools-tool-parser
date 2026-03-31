package org.cru.godtools.shared.renderer.article

import com.github.ajalt.colormath.model.RGB
import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.tool.parser.model.Category
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Text

class RenderArticleCategoryPaparazziTest : BasePaparazziTest() {
    @Test
    fun `RenderArticleCategory()`() = contentSnapshot {
        RenderArticleCategory(
            Category(
                manifest,
                label = { Text(it, text = "Category Label") },
                banner = "bruce",
            ),
        )
    }

    @Test
    fun `RenderArticleCategory() - Label Color`() = contentSnapshot {
        RenderArticleCategory(
            Category(
                manifest,
                label = { Text(it, text = "White Label", textColor = RGB(1, 1, 1)) },
                banner = "bruce",
            ),
        )
    }

    @Test
    fun `RenderArticleCategory() - No Label`() = contentSnapshot {
        RenderArticleCategory(Category(manifest, banner = "bruce"))
    }
}
