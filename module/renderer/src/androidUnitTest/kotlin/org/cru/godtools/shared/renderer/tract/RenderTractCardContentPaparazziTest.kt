package org.cru.godtools.shared.renderer.tract

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.ajalt.colormath.model.RGB
import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.tract.TractPage

class RenderTractCardContentPaparazziTest : BasePaparazziTest() {
    @Test
    fun `RenderTractCardContent() - Background`() = contentSnapshot {
        ElevatedCard(Modifier.padding(16.dp).fillMaxSize()) {
            RenderTractCardContent(
                TractPage.Card(
                    page = TractPage(manifest),
                    backgroundColor = RGB("#dadada"),
                    backgroundImage = "black_panther",
                    content = { card ->
                        listOf(
                            Text(card, text = "Card Line 1"),
                            Text(card, text = "Card Line 2"),
                            Text(card, text = "Card Line 3")
                        )
                    }
                ),
            )
        }
    }
}
