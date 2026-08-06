package org.cru.godtools.shared.renderer.tips

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import kotlin.test.Test
import kotlinx.coroutines.runBlocking
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.tips.Tip
import org.cru.godtools.shared.tool.parser.model.tips.TipPage

@OptIn(ExperimentalMaterial3Api::class)
class RenderTipBottomSheetPaparazziTest : BasePaparazziTest() {
    // start the sheet already expanded so the static snapshot captures the fully presented sheet
    private val expandedSheetState = SheetState(
        skipPartiallyExpanded = true,
        positionalThreshold = { 0f },
        velocityThreshold = { 0f },
        initialValue = SheetValue.Expanded,
    )

    private fun tip(pageCount: Int = 3) = Tip(manifest, id = "training", type = Tip.Type.ASK) {
        List(pageCount) { i ->
            TipPage(it, position = i) { page -> listOf(Text(page, text = "Tip page ${i + 1} content")) }
        }
    }

    @Test
    fun `RenderTipBottomSheet()`() = contentSnapshot {
        RenderTipBottomSheet(tip(), onDismiss = {}, sheetState = expandedSheetState)
    }

    @Test
    fun `RenderTipBottomSheet() - Last Page`() = contentSnapshot {
        RenderTipBottomSheet(tip(pageCount = 1), onDismiss = {}, sheetState = expandedSheetState)
    }

    @Test
    fun `RenderTipBottomSheet() - Completed Tip`() {
        runBlocking { tipsRepository.markTipComplete(manifest.code!!, manifest.locale!!, "training") }
        contentSnapshot {
            RenderTipBottomSheet(tip(), onDismiss = {}, sheetState = expandedSheetState)
        }
    }

    @Test
    fun `RenderTipBottomSheet() - RTL`() = contentSnapshot {
        RenderTipBottomSheet(
            Tip(rtlManifest, id = "training", type = Tip.Type.ASK) {
                List(3) { i ->
                    TipPage(it, position = i) { page -> listOf(Text(page, text = "محتوى الصفحة ${i + 1}")) }
                }
            },
            onDismiss = {},
            sheetState = expandedSheetState
        )
    }
}
