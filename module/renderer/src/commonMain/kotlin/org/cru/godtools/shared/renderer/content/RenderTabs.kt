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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.cru.godtools.shared.renderer.content.extensions.visibility
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Tabs

private val CORNER_RADIUS = 10.dp

@Composable
internal fun RenderTabs(tabs: Tabs, state: State, modifier: Modifier = Modifier) {
    val invisible by remember(tabs, state) {
        tabs.isInvisibleFlow(state)
    }.collectAsState(tabs.isInvisible(state))

    val selectedIndex = remember { mutableStateOf(0) }

    var firstTab: Tabs.Tab? = tabs.tabs.firstOrNull()
    val borderColor: Color = firstTab?.manifest?.primaryColor?.toComposeColor() ?: Color.Black

    SecondaryTabRow(
        selectedTabIndex = selectedIndex.value,
        modifier = modifier
            .visibility(model = tabs, state = state)
            .border(
                BorderStroke(width = 2.dp, borderColor), // Define border width and color
                RoundedCornerShape(CORNER_RADIUS) // Define the rounded corners
            )
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
                val selectedColor: Color = tab.manifest.primaryColor.toComposeColor()
                val unselectedColor: Color = tab.manifest.primaryTextColor.toComposeColor()
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
