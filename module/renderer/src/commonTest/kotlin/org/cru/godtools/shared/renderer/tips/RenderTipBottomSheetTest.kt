package org.cru.godtools.shared.renderer.tips

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import io.fluidsonic.locale.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.BaseRendererTest
import org.cru.godtools.shared.renderer.generated.resources.Res
import org.cru.godtools.shared.renderer.generated.resources.tool_renderer_tip_accessibility_action_close
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.tips.Tip
import org.cru.godtools.shared.tool.parser.model.tips.TipPage
import org.jetbrains.compose.resources.getString

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalTestApi::class)
class RenderTipBottomSheetTest : BaseRendererTest() {
    private val manifest = Manifest(code = "tool", locale = Locale.forLanguageTag("en"))
    private val tip = Tip(manifest, id = "tip1", type = Tip.Type.ASK) { tip ->
        listOf(TipPage(tip, position = 0) { page -> listOf(Text(page, text = "Tip Page 1")) })
    }

    private var dismissed = 0

    private val closeTip by lazy { runBlocking { getString(Res.string.tool_renderer_tip_accessibility_action_close) } }

    @Composable
    private fun TestRenderTipBottomSheet() {
        ProvideTestCompositionLocals {
            RenderTipBottomSheet(tip, onDismiss = { dismissed++ }, state = state)
        }
    }

    @Test
    fun `UI - Renders the tip content in the sheet`() = runComposeUiTest {
        setContent { TestRenderTipBottomSheet() }

        onNodeWithText("Tip Page 1").assertIsDisplayed()
        onNodeWithText("Ask").assertIsDisplayed()
    }

    @Test
    fun `Action - X button triggers onDismiss`() = runComposeUiTest {
        setContent { TestRenderTipBottomSheet() }

        onNodeWithContentDescription(closeTip).performClick()
        assertEquals(1, dismissed)
    }
}
