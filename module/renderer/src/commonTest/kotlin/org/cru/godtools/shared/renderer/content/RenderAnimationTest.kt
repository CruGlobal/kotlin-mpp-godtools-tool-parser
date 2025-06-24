package org.cru.godtools.shared.renderer.content

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher.Companion.expectValue
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.TestResources
import org.cru.godtools.shared.renderer.util.ProvideRendererServices
import org.cru.godtools.shared.tool.parser.model.Animation
import org.cru.godtools.shared.tool.parser.model.Manifest

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class RenderAnimationTest : BaseRenderContentTest() {
    private companion object {
        val MATCHER_IS_PLAYING = expectValue(ANIMATION_IS_PLAYING, true)
    }

    private val manifest = Manifest(resources = { TestResources.resources })

    override val testModel = Animation(
        manifest,
        "kotlin_anim",
        invisibleIf = invisibleIf,
        goneIf = goneIf,
        events = clickableEvents,
        url = clickableUrl,
    )

    override fun SemanticsNodeInteractionsProvider.onModelNode() = onNodeWithTag(TEST_TAG_ANIMATION)

    @Test
    fun `UI - AutoPlay - True`() = runComposeUiTest {
        // TODO: This test is currently disabled since we can't control loading of animations for tests
        //       https://github.com/alexzhirkevich/compottie/issues/45
        return@runComposeUiTest

        setContent {
            ProvideTestCompositionLocals {
                ProvideRendererServices(TestResources.fileSystem) {
                    RenderContentStack(
                        listOf(
                            Animation(
                                parent = manifest,
                                resource = "kotlin_anim",
                                autoPlay = true,
                            )
                        ),
                        state = state,
                    )
                }
            }
        }
        onModelNode().assert(MATCHER_IS_PLAYING)
    }
}
