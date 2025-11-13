package org.cru.godtools.shared.renderer.content

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.cru.godtools.shared.renderer.ToolTheme.ContentHorizontalPadding
import org.cru.godtools.shared.renderer.content.extensions.produceIsInvisible
import org.cru.godtools.shared.renderer.content.extensions.triggerAnalyticsEvents
import org.cru.godtools.shared.renderer.content.extensions.visibility
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.renderer.util.ContentEventListener
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.Tabs
import org.cru.godtools.shared.tool.parser.model.primaryColor
import org.cru.godtools.shared.tool.parser.model.primaryTextColor
import org.cru.godtools.shared.tool.parser.model.stylesParent

internal const val TestTagTabs = "tabs"
internal const val TestTagTab = "tab"

@Composable
internal fun ColumnScope.RenderTabs(tabs: Tabs, state: State, modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()

    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    val isInvisible by tabs.produceIsInvisible(state)
    val borderColor = tabs.stylesParent.primaryColor.toComposeColor()
    val borderShape = MaterialTheme.shapes.small
    val selectedTab by remember(tabs) { derivedStateOf { tabs.tabs.getOrNull(selectedIndex) } }

    ContentEventListener(state, tabs) { event ->
        tabs.tabs.firstOrNull { event in it.listeners }?.let {
            selectedIndex = it.position
        }
    }

    SecondaryTabRow(
        selectedTabIndex = selectedIndex,
        containerColor = Color.White,
        contentColor = Color.White,
        indicator = @Composable {},
        divider = @Composable {},
        modifier = modifier
            .testTag(tag = TestTagTabs)
            .visibility(model = tabs, state = state)
            .padding(horizontal = ContentHorizontalPadding)
            .border(width = 2.dp, borderColor, shape = borderShape)
            // Needed to clip SecondaryTabRow when applying border modifier. ~Levi
            .clip(borderShape)
    ) {
        tabs.tabs.forEachIndexed { index, tab ->
            val isSelected = index == selectedIndex
            val primaryColor = tab.stylesParent.primaryColor.toComposeColor()
            val primaryTextColor = tab.stylesParent.primaryTextColor.toComposeColor()

            Tab(
                selected = isSelected,
                onClick = {
                    tab.triggerAnalyticsEvents(AnalyticsEvent.Trigger.CLICKED, state, coroutineScope)
                    selectedIndex = index
                },
                text = tab.label?.let { { Text(it.text) } },
                enabled = !isInvisible,
                selectedContentColor = primaryTextColor,
                unselectedContentColor = primaryColor,
                modifier = Modifier
                    .testTag(TestTagTab)
                    .background(if (isSelected) primaryColor else primaryTextColor)
            )
        }
    }

    AnimatedContent(
        selectedTab,
        transitionSpec = { fadeIn(tween(0)) togetherWith fadeOut(tween(0)) },
        modifier = Modifier.visibility(tabs, state)
    ) {
        Column {
            RenderContent(
                content = it?.content.orEmpty(),
                state = state,
            )
        }
    }
}
