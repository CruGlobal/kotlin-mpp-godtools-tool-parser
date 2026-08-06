package org.cru.godtools.shared.renderer.tips

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import io.fluidsonic.locale.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.BaseRendererTest
import org.cru.godtools.shared.renderer.generated.resources.Res
import org.cru.godtools.shared.renderer.generated.resources.tool_renderer_tip_accessibility_action_close
import org.cru.godtools.shared.renderer.generated.resources.tool_renderer_tip_action_close
import org.cru.godtools.shared.renderer.generated.resources.tool_renderer_tip_action_next
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.tips.Tip
import org.cru.godtools.shared.tool.parser.model.tips.TipPage
import org.jetbrains.compose.resources.getString

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class RenderTipTest : BaseRendererTest() {
    private val tip = Tip(
        manifest = Manifest(code = "tool", locale = Locale.forLanguageTag("en")),
        id = "tip1",
        type = Tip.Type.ASK,
    ) { tip ->
        List(3) { i -> TipPage(tip, position = i) { page -> listOf(Text(page, text = "Tip Page ${i + 1}")) } }
    }

    private var dismissed = 0

    private val next by lazy { runBlocking { getString(Res.string.tool_renderer_tip_action_next) } }
    private val close by lazy { runBlocking { getString(Res.string.tool_renderer_tip_action_close) } }
    private val closeTip by lazy { runBlocking { getString(Res.string.tool_renderer_tip_accessibility_action_close) } }

    @Composable
    private fun TestRenderTip(tip: Tip = this.tip) {
        ProvideTestCompositionLocals {
            RenderTip(tip, state = state, onDismiss = { dismissed++ }, modifier = Modifier.fillMaxSize())
        }
    }

    private fun SemanticsNodeInteractionsProvider.onPageButton(position: Int) = onNode(
        hasTestTag(TestTagTipPageButton) and hasAnyAncestor(SemanticsMatcher.expectValue(TipPagePosition, position)),
    )

    @Test
    fun `UI - Header - Tip type label`() = runComposeUiTest {
        setContent { TestRenderTip() }
        onNodeWithText("Ask").assertIsDisplayed()
    }

    @Test
    fun `UI - Page content is rendered`() = runComposeUiTest {
        setContent { TestRenderTip() }
        onNodeWithText("Tip Page 1").assertIsDisplayed()
    }

    @Test
    fun `UI - Button - Next on non-last pages and Close on last page`() = runComposeUiTest {
        setContent { TestRenderTip() }

        onPageButton(0).assertTextEquals(next)
        onPageButton(0).performClick()
        onPageButton(1).assertTextEquals(next)
        onPageButton(1).performClick()
        onPageButton(2).assertTextEquals(close)
    }

    @Test
    fun `Action - Next button advances to the next page`() = runComposeUiTest {
        setContent { TestRenderTip() }

        onPageButton(0).performClick()
        onNodeWithText("Tip Page 2").assertIsDisplayed()
        assertEquals(0, dismissed)
    }

    @Test
    fun `Action - Close button on last page dismisses the tip`() = runComposeUiTest {
        setContent { TestRenderTip() }

        onPageButton(0).performClick()
        onPageButton(1).performClick()
        onPageButton(2).performClick()
        assertEquals(1, dismissed)
    }

    @Test
    fun `Action - X button dismisses the tip`() = runComposeUiTest {
        setContent { TestRenderTip() }

        onNodeWithContentDescription(closeTip).assertIsDisplayed().performClick()
        assertEquals(1, dismissed)
    }

    @Test
    fun `Action - Close button on last page marks the tip complete`() = runComposeUiTest {
        setContent { TestRenderTip() }

        onPageButton(0).performClick()
        onPageButton(1).performClick()
        onPageButton(2).performClick()
        waitForIdle()

        assertEquals(1, dismissed)
        assertTrue(tipsRepository.isTipCompleteFlow(tip.manifest.code!!, Locale.forLanguageTag("en"), tip.id).first())
    }

    @Test
    fun `Action - X button does not mark the tip complete`() = runComposeUiTest {
        setContent { TestRenderTip() }

        onNodeWithContentDescription(closeTip).performClick()
        waitForIdle()

        assertEquals(1, dismissed)
        assertFalse(tipsRepository.isTipCompleteFlow(tip.manifest.code!!, Locale.forLanguageTag("en"), tip.id).first())
    }

    @Test
    fun `Action - Close button skips marking complete when manifest has no code or locale`() = runComposeUiTest {
        val tip = Tip(id = "tip1", type = Tip.Type.ASK) { tip ->
            listOf(TipPage(tip, position = 0) { page -> listOf(Text(page, text = "Only Page")) })
        }
        setContent { TestRenderTip(tip) }

        onPageButton(0).performClick()
        waitForIdle()

        assertEquals(1, dismissed)
        assertFalse(tipsRepository.isTipCompleteFlow("tool", Locale.forLanguageTag("en"), tip.id).first())
    }
}
