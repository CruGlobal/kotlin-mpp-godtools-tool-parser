package org.cru.godtools.shared.renderer.tips

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.SemanticsMatcher
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import me.saket.touchrobot.onNode
import me.saket.touchrobot.rememberTouchRobot
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.tips.Tip
import org.cru.godtools.shared.tool.parser.model.tips.TipPage

// These tests exercise RenderTip directly instead of RenderTipBottomSheet because simulated touch events are unable to
// reach the ModalBottomSheet popup window when rendered within Paparazzi.
class RenderTipPaparazziTest : BasePaparazziTest() {
    @Test
    fun `RenderTip() - Page Swipe`() {
        val tip = Tip(manifest, id = "training", type = Tip.Type.ASK) {
            List(3) { i ->
                TipPage(it, position = i) { page -> listOf(Text(page, text = "Tip page ${i + 1} content")) }
            }
        }

        animatedContentSnapshot(end = 1_000) {
            RenderTip(tip, modifier = Modifier.fillMaxSize())

            val touchRobot = rememberTouchRobot()
            LaunchedEffect(Unit) {
                delay(200.milliseconds)
                touchRobot.onNode(SemanticsMatcher.expectValue(TipPagePosition, 0)).performGesture {
                    swipe(start = centerRight, stop = centerLeft, duration = 600.milliseconds)
                }
            }
        }
    }

    @Test
    fun `RenderTip() - Page Swipe - RTL`() {
        val tip = Tip(rtlManifest, id = "training", type = Tip.Type.ASK) {
            List(3) { i ->
                TipPage(it, position = i) { page -> listOf(Text(page, text = "محتوى الصفحة ${i + 1}")) }
            }
        }

        animatedContentSnapshot(end = 1_000) {
            RenderTip(tip, modifier = Modifier.fillMaxSize())

            val touchRobot = rememberTouchRobot()
            LaunchedEffect(Unit) {
                delay(200.milliseconds)
                touchRobot.onNode(SemanticsMatcher.expectValue(TipPagePosition, 0)).performGesture {
                    swipe(start = centerLeft, stop = centerRight, duration = 600.milliseconds)
                }
            }
        }
    }
}
