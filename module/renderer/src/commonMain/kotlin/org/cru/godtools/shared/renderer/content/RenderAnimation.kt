package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import org.cru.godtools.shared.renderer.content.extensions.Resource
import org.cru.godtools.shared.renderer.content.extensions.clickable
import org.cru.godtools.shared.renderer.content.extensions.visibility
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.renderer.util.LocalResourceFileSystem
import org.cru.godtools.shared.tool.parser.model.Animation

@Composable
internal fun ColumnScope.RenderAnimation(animation: Animation, state: State) {
    val resource = animation.resource?.takeUnless { it.localName == null } ?: return

    val coroutineScope = rememberCoroutineScope()
    val fileSystem = LocalResourceFileSystem.current

    val composition by rememberLottieComposition { LottieCompositionSpec.Resource(fileSystem, resource) }
    var isPlaying by remember { mutableStateOf(animation.autoPlay) }
    var iterations by remember { mutableIntStateOf(1) }

    val animationState = animateLottieCompositionAsState(
        composition,
        isPlaying = isPlaying,
        iterations = if (animation.loop) Compottie.IterateForever else iterations,
        restartOnPlay = false,
    )

    LaunchedEffect(animation, state) {
        // handle play/stop listeners
        state.contentEvents
            .collect {
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
    }

    Image(
        painter = rememberLottiePainter(composition, progress = { animationState.value }),
        contentDescription = null,
        modifier = Modifier
            .visibility(animation, state)
            .padding(horizontal = Horizontal_Padding)
            .fillMaxWidth()
            .clickable(animation, state, coroutineScope)
    )
}
