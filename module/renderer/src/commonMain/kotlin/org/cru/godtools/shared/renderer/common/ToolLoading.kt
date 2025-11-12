package org.cru.godtools.shared.renderer.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.cru.godtools.shared.renderer.ToolTheme.GodToolsGreen
import org.cru.godtools.shared.renderer.ToolTheme.ProgressBarGapSize
import org.cru.godtools.shared.renderer.ToolTheme.ProgressBarHeight
import org.cru.godtools.shared.renderer.generated.resources.Res
import org.cru.godtools.shared.renderer.generated.resources.ic_tool_loading_checkmark
import org.cru.godtools.shared.renderer.generated.resources.tool_loading
import org.cru.godtools.shared.tool.parser.model.primaryColor
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ToolLoading(progress: Float?, modifier: Modifier = Modifier) {
    CompositionLocalProvider(
        LocalContentColor provides null.primaryColor.toComposeColor(),
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(32.dp),
        ) {
            Icon(
                painterResource(Res.drawable.ic_tool_loading_checkmark),
                contentDescription = null,
                tint = GodToolsGreen,
                modifier = Modifier.size(90.dp)
            )

            val progressModifier = Modifier
                .padding(top = 20.dp)
                .widthIn(max = 200.dp)
                .height(ProgressBarHeight)
            when (progress) {
                null -> LinearProgressIndicator(
                    color = LocalContentColor.current,
                    trackColor = LocalContentColor.current.let { it.copy(alpha = it.alpha * 0.24f) },
                    gapSize = ProgressBarGapSize,
                    modifier = progressModifier
                )
                else -> {
                    val progress by animateFloatAsState(progress)
                    LinearProgressIndicator(
                        progress = { progress },
                        color = LocalContentColor.current,
                        trackColor = LocalContentColor.current.let { it.copy(alpha = it.alpha * 0.24f) },
                        gapSize = ProgressBarGapSize,
                        drawStopIndicator = {},
                        modifier = progressModifier
                    )
                }
            }

            Text(
                text = stringResource(Res.string.tool_loading),
                style = MaterialTheme.typography.headlineSmall.copy(lineBreak = LineBreak.Heading),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp),
            )
        }
    }
}
