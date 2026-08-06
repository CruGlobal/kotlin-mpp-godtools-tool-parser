package org.cru.godtools.shared.renderer.tips

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.ccci.gto.android.common.compose.foundation.verticalFadingEdgeEffect
import org.cru.godtools.shared.renderer.ToolTheme
import org.cru.godtools.shared.renderer.content.RenderContent
import org.cru.godtools.shared.renderer.generated.resources.Res
import org.cru.godtools.shared.renderer.generated.resources.tool_renderer_tip_action_close
import org.cru.godtools.shared.renderer.generated.resources.tool_renderer_tip_action_next
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.analytics.ToolAnalyticsScreenNames
import org.cru.godtools.shared.tool.parser.model.primaryColor
import org.cru.godtools.shared.tool.parser.model.primaryTextColor
import org.cru.godtools.shared.tool.parser.model.stylesParent
import org.cru.godtools.shared.tool.parser.model.tips.TipPage
import org.jetbrains.compose.resources.stringResource

internal const val TestTagTipPageButton = "TipPageButton"

internal val TipPagePosition = SemanticsPropertyKey<Int>(
    name = "TipPagePosition",
    mergePolicy = { parentValue, _ ->
        // Never merge TipPagePosition, to avoid leaking internal semantics to parents.
        parentValue
    },
)

@Composable
internal fun RenderTipPage(
    page: TipPage,
    state: State,
    onNextPage: () -> Unit,
    onCloseTip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LifecycleResumeEffect(page, state) {
        state.triggerEvent(
            State.Event.AnalyticsEvent.ScreenView(
                tool = page.manifest.code,
                locale = page.manifest.locale,
                screenName = ToolAnalyticsScreenNames.forTipPage(page),
            ),
        )
        onPauseOrDispose { }
    }

    Column(
        modifier = modifier.semantics { this[TipPagePosition] = page.position }
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .verticalFadingEdgeEffect(scrollState)
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp)
        ) {
            ProvideTextStyle(ToolTheme.ContentTextStyle) {
                RenderContent(page.content, state = state)
            }
        }

        Button(
            onClick = { if (page.isLastPage) onCloseTip() else onNextPage() },
            colors = ButtonDefaults.buttonColors(
                containerColor = page.stylesParent.primaryColor.toComposeColor(),
                contentColor = page.stylesParent.primaryTextColor.toComposeColor(),
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 64.dp)
                .padding(bottom = 16.dp)
                .testTag(TestTagTipPageButton)
        ) {
            Text(
                when {
                    page.isLastPage -> stringResource(Res.string.tool_renderer_tip_action_close)
                    else -> stringResource(Res.string.tool_renderer_tip_action_next)
                }
            )
        }
    }
}
