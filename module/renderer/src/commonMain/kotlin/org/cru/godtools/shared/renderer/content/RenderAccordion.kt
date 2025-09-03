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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Accordion

@Composable
fun RenderAccordion(accordion: Accordion, state: State, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        accordion.sections.forEachIndexed { index, section ->
            RenderAccordionSection(section, state)
        }
    }
}

@Composable
private fun RenderAccordionSection(section: Accordion.Section, state: State, modifier: Modifier = Modifier) {
    val headerHeight = 50.dp
    val isExpanded = false

    Card(
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
                    Text(
                        "Title Header",
                        color = Color.Black
                    )

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

            if (isExpanded) {
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
