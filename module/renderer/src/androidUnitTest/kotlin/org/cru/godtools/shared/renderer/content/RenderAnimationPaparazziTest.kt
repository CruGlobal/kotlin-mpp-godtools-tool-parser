package org.cru.godtools.shared.renderer.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.InternalCompottieApi
import io.github.alexzhirkevich.compottie.ioDispatcher
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.tool.parser.model.Animation

private const val COMPOTTIE_IO_DISPATCHER_CLASS = "io.github.alexzhirkevich.compottie.IoDispatcher_jvmNativeKt"

@OptIn(ExperimentalCoroutinesApi::class, InternalCompottieApi::class)
class RenderAnimationPaparazziTest : BasePaparazziTest() {
    @BeforeTest
    fun setupCompottie() {
        mockkStatic(COMPOTTIE_IO_DISPATCHER_CLASS)
    }

    @AfterTest
    fun cleanupCompottie() {
        unmockkStatic(COMPOTTIE_IO_DISPATCHER_CLASS)
    }

    @Test
    fun `RenderAnimation() - dotLottie`() = animatedContentSnapshot(end = 1_500) {
        SetCompottieIoDispatcher()

        RenderContentStack(
            listOf(
                Animation(manifest, "nyan_cat")
            ),
        )
    }

    @Test
    fun `RenderAnimation() - Lottie`() = animatedContentSnapshot(end = 1_500) {
        SetCompottieIoDispatcher()

        RenderContentStack(
            listOf(
                Animation(manifest, "kotlin_anim"),
            ),
        )
    }

    @Composable
    @OptIn(ExperimentalStdlibApi::class)
    private fun SetCompottieIoDispatcher() {
        rememberCoroutineScope().coroutineContext[CoroutineDispatcher.Key]?.let {
            every { Compottie.ioDispatcher() } returns it
        }
    }
}
