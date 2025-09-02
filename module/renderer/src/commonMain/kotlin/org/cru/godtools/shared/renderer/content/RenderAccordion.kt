package org.cru.godtools.shared.renderer.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Accordion

@Composable
fun RenderAccordion(accordion: Accordion, state: State, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        RenderAccordionSection()

        RenderAccordionSection()

        RenderAccordionSection()
    }
}
