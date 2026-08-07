package org.cru.godtools.shared.renderer.tips

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.rememberLifecycleOwner
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.cru.godtools.shared.renderer.content.extensions.stringTipType
import org.cru.godtools.shared.renderer.generated.resources.Res
import org.cru.godtools.shared.renderer.generated.resources.tool_renderer_tip_accessibility_action_close
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.renderer.util.ProvideLayoutDirectionFromLocale
import org.cru.godtools.shared.tool.parser.model.tips.Tip
import org.jetbrains.compose.resources.stringResource

private val TipAppBarHeight = 64.dp
private val TipProgressBarHeight = 12.dp
private val TipProgressBarGapSize = 0.dp - TipProgressBarHeight
private val TipHeaderHorizontalPadding = 32.dp

/**
 * Render the full content of a training [tip]: close button, page progress, tip type header, and a pager of the tip's
 * pages, each page ending with a pinned Next/Close button.
 *
 * This composable fills the height it is given, so it should be sized by the caller (e.g. `Modifier.fillMaxSize()` or
 * a bounded container such as [RenderTipBottomSheet]).
 *
 * @param onDismiss triggered when the user closes the tip, either via the close button or by completing the last page.
 */
@Composable
fun RenderTip(
    tip: Tip,
    modifier: Modifier = Modifier,
    state: State = remember { State() },
    onDismiss: () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    val tipsRepository = LocalTipsRepository.current
    val pagerState = rememberPagerState { tip.pages.size }

    ProvideLayoutDirectionFromLocale(tip.manifest.locale) {
        Column(modifier) {
            TipAppBar(tip, pagerState, onDismiss = onDismiss)
            TipHeader(tip, modifier = Modifier.padding(top = 8.dp))

            HorizontalPager(
                pagerState,
                beyondViewportPageCount = 1,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 16.dp)
            ) { i ->
                val page = tip.pages[i]
                val isCurrentPage by remember { derivedStateOf { i == pagerState.currentPage } }
                val lifecycleOwner = rememberLifecycleOwner(
                    maxLifecycle = if (isCurrentPage) Lifecycle.State.RESUMED else Lifecycle.State.STARTED,
                )

                CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner) {
                    RenderTipPage(
                        page,
                        state = state,
                        onNextPage = { coroutineScope.launch { pagerState.animateScrollToPage(i + 1) } },
                        onCloseTip = {
                            val tool = tip.manifest.code
                            val locale = tip.manifest.locale
                            if (tool != null && locale != null) {
                                coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
                                    withContext(NonCancellable) {
                                        tipsRepository.markTipComplete(tool, locale, tip.id)
                                    }
                                }
                            }
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun TipAppBar(tip: Tip, pagerState: PagerState, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    val primaryColor = tip.primaryColor.toComposeColor()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(TipAppBarHeight)
    ) {
        IconButton(
            onClick = onDismiss,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Icon(
                Icons.Filled.Close,
                contentDescription = stringResource(Res.string.tool_renderer_tip_accessibility_action_close),
                tint = primaryColor,
            )
        }

        LinearProgressIndicator(
            progress = {
                when (val pageCount = pagerState.pageCount) {
                    0 -> 0f
                    else -> (pagerState.currentPage + 1 + pagerState.currentPageOffsetFraction) / pageCount
                }
            },
            color = primaryColor,
            trackColor = primaryColor.copy(alpha = primaryColor.alpha * 0.24f),
            gapSize = TipProgressBarGapSize,
            drawStopIndicator = {},
            modifier = Modifier
                .height(TipProgressBarHeight)
                .weight(1f)
        )

        // Match the width of the Close IconButton w/ padding
        Spacer(Modifier.width(64.dp))
    }
}

@Composable
private fun TipHeader(tip: Tip, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = TipHeaderHorizontalPadding)
    ) {
        TipIcon(tip)
        Text(
            stringTipType(tip.type),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = tip.textColor.toComposeColor(),
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}
