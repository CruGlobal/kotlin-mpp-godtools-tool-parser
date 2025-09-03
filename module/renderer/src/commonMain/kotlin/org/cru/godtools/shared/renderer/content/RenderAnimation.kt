@file:Suppress("ktlint:compose:compositionlocal-allowlist")

package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.InternalCompottieApi
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.ioDispatcher
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import kotlin.coroutines.CoroutineContext
import org.cru.godtools.shared.renderer.content.extensions.Resource
import org.cru.godtools.shared.renderer.content.extensions.clickable
import org.cru.godtools.shared.renderer.content.extensions.visibility
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.renderer.util.ContentEventListener
import org.cru.godtools.shared.renderer.util.LocalResourceFileSystem
import org.cru.godtools.shared.tool.parser.model.Animation

internal const val TestTagAnimation = "animation"
internal val AnimationIsPlaying = SemanticsPropertyKey<Boolean>(
    name = "AnimationIsPlaying",
    mergePolicy = { parentValue, _ ->
        // Never merge TestTags, to avoid leaking internal test tags to parents.
        parentValue
    }
)

@OptIn(InternalCompottieApi::class)
internal val LocalCompottieCoroutineContext = staticCompositionLocalOf<CoroutineContext> { Compottie.ioDispatcher() }

@Composable
internal fun ColumnScope.RenderAnimation(animation: Animation, state: State) {
    val resource = animation.resource?.takeUnless { it.localName == null } ?: return

    val coroutineScope = rememberCoroutineScope()
    val fileSystem = LocalResourceFileSystem.current

    val composition by rememberLottieComposition(coroutineContext = LocalCompottieCoroutineContext.current) {
        LottieCompositionSpec.Resource(fileSystem, resource)
    }
    var isPlaying by remember { mutableStateOf(animation.autoPlay) }
    var iterations by remember { mutableIntStateOf(1) }

    val animationState = animateLottieCompositionAsState(
        composition,
        isPlaying = isPlaying,
        iterations = if (animation.loop) Compottie.IterateForever else iterations,
        restartOnPlay = false,
    )

    ContentEventListener(state, animation) {
        when {
            it in animation.playListeners && !animationState.isPlaying -> {
                if (animationState.isAtEnd && animationState.progress > 0f) {
                    iterations = animationState.iteration + 1
                }
                isPlaying = true
            }
            it in animation.stopListeners && animationState.isPlaying -> isPlaying = false
        }
    }

    Image(
        painter = rememberLottiePainter(composition, progress = { animationState.value }),
        contentDescription = null,
        modifier = Modifier
            .visibility(animation, state)
            .padding(horizontal = HorizontalPadding)
            .fillMaxWidth()
            .clickable(animation, state, coroutineScope, indication = null)
            .testTag(TestTagAnimation)
            .semantics { set(AnimationIsPlaying, animationState.isPlaying) },
    )
}
