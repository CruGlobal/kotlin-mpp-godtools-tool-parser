@file:OptIn(ExperimentalMaterial3Api::class)

package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.cru.godtools.shared.renderer.content.extensions.visibility
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Tabs
import org.cru.godtools.shared.tool.parser.model.stylesParent

private val CORNER_RADIUS = 10.dp
private val DEFAULT_SELECTED_COLOR = Color.DarkGray
private val DEFAULT_UNSELECTED_COLOR = Color.White

@Composable
internal fun RenderTabs(tabs: Tabs, state: State, modifier: Modifier = Modifier) {
    val invisible by remember(tabs, state) {
        tabs.isInvisibleFlow(state)
    }.collectAsState(tabs.isInvisible(state))

    val lifecycleOwner = LocalLifecycleOwner.current
    val borderColor: Color = tabs.stylesParent?.primaryColor?.toComposeColor() ?: DEFAULT_SELECTED_COLOR

    var selectedIndex = remember { mutableIntStateOf(0) }

    LaunchedEffect(tabs, state) {
        // handle play/stop listeners
        state.contentEvents
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.RESUMED)
            .collect {
                for (tabIndex in 0..<tabs.tabs.count()) {
                    val isSelectedTab: Boolean = it in tabs.tabs[tabIndex].listeners
                    if (isSelectedTab) {
                        selectedIndex.value = tabIndex
                        break
                    }
                }
            }
    }

    SecondaryTabRow(
        selectedTabIndex = selectedIndex.value,
        modifier = modifier
            .visibility(model = tabs, state = state)
            .border(
                BorderStroke(width = 2.dp, borderColor),
                RoundedCornerShape(CORNER_RADIUS)
            )
            // Needed to clip SecondaryTabRow when applying border modifier. ~Levi
            .clip(RoundedCornerShape(CORNER_RADIUS)),
        containerColor = Color.White,
        contentColor = Color.White,
        indicator = @Composable {
        },
        divider = @Composable {
        },
        tabs = @Composable {
            tabs.tabs.forEachIndexed { index, tab ->

                val isSelected: Boolean = index == selectedIndex.value
                val selectedColor: Color = tab.stylesParent?.primaryColor?.toComposeColor()
                    ?: DEFAULT_SELECTED_COLOR
                val unselectedColor: Color = tab.stylesParent?.primaryTextColor?.toComposeColor()
                    ?: DEFAULT_UNSELECTED_COLOR
                val backgroundColor: Color = if (isSelected) selectedColor else unselectedColor

                Tab(
                    selected = isSelected,
                    onClick = {
                        selectedIndex.value = index
                    },
                    modifier = Modifier
                        .background(backgroundColor),
                    text = {
                        tab.label?.let {
                            Text(
                                text = it.text
                            )
                        }
                    },
                    enabled = !invisible,
                    selectedContentColor = unselectedColor,
                    unselectedContentColor = selectedColor
                )
            }
        }
    )
}
