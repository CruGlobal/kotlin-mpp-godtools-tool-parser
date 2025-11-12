package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.model.VideoPlayerConfig
import chaintech.videoplayer.ui.youtube.YouTubePlayerComposable
import org.cru.godtools.shared.renderer.ToolTheme.ContentHorizontalPadding
import org.cru.godtools.shared.renderer.content.extensions.alignment
import org.cru.godtools.shared.renderer.content.extensions.visibility
import org.cru.godtools.shared.renderer.content.extensions.width
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Video

internal const val TestTagVideo = "video"

@Composable
internal fun ColumnScope.RenderVideo(model: Video, state: State) = when (model.provider) {
    Video.Provider.YOUTUBE -> {
        val playerHost = remember {
            MediaPlayerHost(
                mediaUrl = model.videoId.orEmpty(),
                isPaused = true,
                isLooping = false
            )
        }.apply { loadUrl(model.videoId.orEmpty()) }
        val playerConfig = remember {
            VideoPlayerConfig(
                isAutoHideControlEnabled = false,
                isFullScreenEnabled = false,
                autoPlayNextReel = false,
            )
        }

        LaunchedEffect(model, state) { model.isInvisibleFlow(state).collect { if (it) playerHost.pause() } }
        LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) { playerHost.pause() }

        Box(
            propagateMinConstraints = true,
            // HACK: YouTubePlayerComposable re-uses the Modifier in multiple nested Composables, this has the effect of
            //       compounding any padding and may cause other unexpected behaviour.
            //       For now we apply the appropriate modifiers to a containing Box
            modifier = Modifier
                .testTag(TestTagVideo)
                .visibility(model, state)
                .padding(horizontal = ContentHorizontalPadding)
                .width(model.width)
                .align(model.gravity.alignment)
                .aspectRatio(model.aspectRatio.ratio.toFloat())
        ) {
            YouTubePlayerComposable(playerHost = playerHost, playerConfig = playerConfig)
        }
    }
    Video.Provider.UNKNOWN -> Unit
}
