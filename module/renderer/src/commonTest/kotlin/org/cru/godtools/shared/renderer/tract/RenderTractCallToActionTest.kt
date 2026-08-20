package org.cru.godtools.shared.renderer.tract

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.BaseRendererTest
import org.cru.godtools.shared.renderer.generated.resources.Res
import org.cru.godtools.shared.renderer.generated.resources.tract_accessibility_action_next_page
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.tract.CallToAction
import org.cru.godtools.shared.tool.parser.model.tract.TractPage
import org.jetbrains.compose.resources.getString

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTestApi::class)
class RenderTractCallToActionTest : BaseRendererTest() {
    private var nextPageCalled = 0

    @Test
    fun `Action - arrow - tap advances to next page`() = runComposeUiTest {
        val manifest = Manifest(
            pages = {
                listOf(
                    TractPage(
                        it,
                        callToAction = { p -> CallToAction(p, label = { c -> Text(c, text = "Keep going") }) },
                    ),
                    TractPage(it),
                )
            },
        )
        val page = manifest.pages.first() as TractPage

        setContent {
            ProvideTestCompositionLocals {
                RenderTractCallToAction(page, onNextPage = { nextPageCalled++ })
            }
        }

        onNodeWithText("Keep going").assertExists("call to action label should render")
        onNodeWithContentDescription(getString(Res.string.tract_accessibility_action_next_page)).performClick()
        assertEquals(1, nextPageCalled, "tapping the arrow should advance to the next page")
    }

    @Test
    fun `UI - arrow - not rendered on last page`() = runComposeUiTest {
        val manifest = Manifest(pages = { listOf(TractPage(it), TractPage(it)) })

        setContent {
            ProvideTestCompositionLocals {
                RenderTractCallToAction(manifest.pages.last() as TractPage, onNextPage = { nextPageCalled++ })
            }
        }

        onNodeWithContentDescription(getString(Res.string.tract_accessibility_action_next_page)).assertDoesNotExist()
    }
}
