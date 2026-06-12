package org.cru.godtools.shared.renderer.content

import io.github.alexzhirkevich.compottie.InternalCompottieApi
import kotlin.test.Test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.tool.parser.model.Animation

@OptIn(ExperimentalCoroutinesApi::class, InternalCompottieApi::class)
class RenderAnimationPaparazziTest : BasePaparazziTest() {
    @Test
    fun `RenderAnimation() - dotLottie`() = animatedContentSnapshot(end = 1_500) {
        RenderContentStack(
            listOf(
                Animation(manifest, "nyan_cat")
            ),
        )
    }

    @Test
    fun `RenderAnimation() - Lottie`() = animatedContentSnapshot(end = 1_500) {
        RenderContentStack(
            listOf(
                Animation(manifest, "kotlin_anim"),
            ),
        )
    }
}
