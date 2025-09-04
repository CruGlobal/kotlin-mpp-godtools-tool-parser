package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Accordion

@Composable
fun RenderAccordion(accordion: Accordion, state: State, modifier: Modifier = Modifier, supportsMultiSelection: Boolean = false) {
    val selectedSections = remember { mutableStateListOf<Int>() }

    Column(
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        accordion.sections.forEachIndexed { index, section ->
            val isSelected = selectedSections.contains(index)

            RenderAccordionSection(section, state, isSelected) {
                if (isSelected) {
                    selectedSections.remove(index)
                }
                else if (supportsMultiSelection) {
                    selectedSections.add(index)
                }
                else {
                    selectedSections.clear()
                    selectedSections.add(index)
                }
            }
        }
    }
}

@Composable
private fun RenderAccordionSection(section: Accordion.Section, state: State, isSelected: Boolean, modifier: Modifier = Modifier, onClicked: () -> Unit) {
    val headerHeight = 50.dp

    Card(
        onClick = {
            onClicked()
        },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .height(headerHeight)
            ) {
                Row(
                    modifier = Modifier
                ) {
                    section.header?.let {
                        Text(
                            it.text,
                            color = Color.Black
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .background(Color.Blue)
                            .size(20.dp, 20.dp)
                    )
                }
            }

            if (isSelected) {
                Column(
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                ) {
                    RenderContent(section.content, state)
                }
            }
        }
    }
}
