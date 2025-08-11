@file:OptIn(ExperimentalMaterial3Api::class)

package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Tabs

private val TAB_SELECTED_COLOR = Color.DarkGray
private val TAB_UNSELECTED_COLOR = Color.LightGray
private val TAB_SELECTED_CONTENT_COLOR = Color.White
private val TAB_UNSELECTED_CONTENT_COLOR = Color.DarkGray

@Composable
internal fun RenderTabs(tabs: Tabs, state: State, modifier: Modifier = Modifier) {
    val selectedIndex = remember { mutableStateOf(0) }

    SecondaryTabRow(
        selectedTabIndex = selectedIndex.value,
        modifier = modifier,
        containerColor = Color.White,
        contentColor = Color.White,
        indicator = @Composable {

        },
        divider = @Composable {

        },
        tabs = @Composable {
            tabs.tabs.forEachIndexed { index, tab ->

                val isSelected: Boolean = index == selectedIndex.value
                val backgroundColor: Color = if (isSelected) TAB_SELECTED_COLOR else TAB_UNSELECTED_COLOR

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
                    enabled = true,
                    selectedContentColor = TAB_SELECTED_CONTENT_COLOR,
                    unselectedContentColor = TAB_UNSELECTED_CONTENT_COLOR
                )
            }
        }
    )
}
