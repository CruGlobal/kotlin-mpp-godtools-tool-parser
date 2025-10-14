package org.cru.godtools.shared.renderer.lesson

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.cru.godtools.shared.renderer.RenderBackground
import org.cru.godtools.shared.renderer.content.RenderContent
import org.cru.godtools.shared.renderer.content.extensions.triggerAnalyticsEvents
import org.cru.godtools.shared.renderer.generated.resources.Res
import org.cru.godtools.shared.renderer.generated.resources.lesson_accessibility_action_page_next
import org.cru.godtools.shared.renderer.generated.resources.lesson_accessibility_action_page_previous
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.analytics.ToolAnalyticsScreenNames
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.lesson.LessonPage
import org.jetbrains.compose.resources.stringResource

internal const val TestTagLessonPage = "LessonPage"
internal val LessonPageId = SemanticsPropertyKey<String>(
    name = "LessonPageId",
    mergePolicy = { parentValue, _ ->
        // Never merge TestTags, to avoid leaking internal test tags to parents.
        parentValue
    }
)

@Composable
fun RenderLessonPage(
    page: LessonPage,
    modifier: Modifier = Modifier,
    contentInsets: PaddingValues = PaddingValues(0.dp),
    state: State = remember { State() },
    scrollState: ScrollState = rememberScrollState(),
    pageEvents: (LessonPageEvent) -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    LifecycleResumeEffect(page, state) {
        state.triggerEvent(
            State.Event.AnalyticsEvent.ScreenView(
                tool = page.manifest.code,
                locale = page.manifest.locale,
                screenName = ToolAnalyticsScreenNames.forLessonPage(page)
            )
        )

        val delayedEvents = page.triggerAnalyticsEvents(AnalyticsEvent.Trigger.VISIBLE, state, coroutineScope)
        onPauseOrDispose { delayedEvents.forEach { it.cancel() } }
    }

    Box(
        modifier
            .testTag(TestTagLessonPage)
            .semantics { this[LessonPageId] = page.id }
    ) {
        RenderBackground(page.background, Modifier.matchParentSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(contentInsets)
                // ensure there is always space to scroll above the navigation controls
                .padding(bottom = 48.dp)
        ) {
            RenderContent(page.content, state = state)
        }

        CompositionLocalProvider(LocalContentColor provides page.controlColor.toComposeColor()) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(contentInsets)
                    .fillMaxWidth()
            ) {
                if (!page.isFirstPage) {
                    IconButton(onClick = { pageEvents(LessonPageEvent.PreviousPage) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            stringResource(Res.string.lesson_accessibility_action_page_previous),
                            Modifier.size(24.dp),
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                if (!page.isLastPage) {
                    IconButton(onClick = { pageEvents(LessonPageEvent.NextPage) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            stringResource(Res.string.lesson_accessibility_action_page_next),
                            Modifier.size(24.dp),
                        )
                    }
                }
            }
        }
    }
}

sealed interface LessonPageEvent {
    data object NextPage : LessonPageEvent
    data object PreviousPage : LessonPageEvent
}
