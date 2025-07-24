package org.cru.godtools.shared.renderer.content

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import app.cash.turbine.test
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.tips.InlineTip
import org.cru.godtools.shared.tool.parser.model.tips.Tip

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class RenderInlineTipTest : BaseRenderContentTest() {
    private val manifest = Manifest(tips = { listOf(Tip(it, id = "tip")) })

    override val testModel = InlineTip(manifest, id = "tip", invisibleIf = invisibleIf, goneIf = goneIf)

    override fun SemanticsNodeInteractionsProvider.onModelNode() = onNodeWithTag(TestTagInlineTip)

    @BeforeTest
    fun enableTipsByDefault() {
        state.showTips.value = true
    }

    @Test
    fun `Action - Click - Triggers OpenTip event`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderContentStack(
                    listOf(testModel),
                    state = state,
                )
            }
        }

        testScope.runTest {
            state.events.filterIsInstance<State.Event.OpenTip>().test {
                onModelNode().assertExists().performClick()
                assertEquals(State.Event.OpenTip("tip"), awaitItem())
            }
        }
    }

    @Test
    fun `UI - Visibility - showTips`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderContentStack(
                    listOf(testModel),
                    state = state,
                )
            }
        }

        onModelNode().assertExists().assertHasClickAction().assertIsEnabled()
        state.showTips.value = false
        onModelNode().assertDoesNotExist()
        state.showTips.value = true
        onModelNode().assertExists().assertHasClickAction().assertIsEnabled()
    }
}
