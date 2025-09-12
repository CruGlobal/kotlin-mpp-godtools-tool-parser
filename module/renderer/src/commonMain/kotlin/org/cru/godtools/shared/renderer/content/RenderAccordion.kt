package org.cru.godtools.shared.renderer.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.cru.godtools.shared.renderer.content.extensions.visibility
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Accordion

internal const val TestTagAccordion = "accordion"
internal const val TestTagAccordionSection = "accordion section"
internal val SectionIsSelected = SemanticsPropertyKey<Boolean>(
    name = "SectionIsSelected",
    mergePolicy = { parentValue, _ ->
        // Never merge TestTags, to avoid leaking internal test tags to parents.
        parentValue
    }
)

@Composable
fun RenderAccordion(
    accordion: Accordion,
    state: State,
    modifier: Modifier = Modifier,
    supportsMultiSelection: Boolean = false
) {
    val selectedSections = remember { mutableStateListOf<String>() }

    Column(
        modifier = modifier
            .testTag(TestTagAccordion)
            .visibility(accordion, state),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        accordion.sections.forEachIndexed { index, section ->
            val isSelected = selectedSections.contains(section.id)

            RenderAccordionSection(section, state, isSelected, {
                if (isSelected) {
                    selectedSections.remove(section.id)
                } else if (supportsMultiSelection) {
                    selectedSections.add(section.id)
                } else {
                    selectedSections.clear()
                    selectedSections.add(section.id)
                }
            })
        }
    }
}

@Composable
private fun RenderAccordionSection(
    section: Accordion.Section,
    state: State,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardColor = Color.White
    val headerHeight = 50.dp

    ElevatedCard(
        onClick = {
            onClick()
        },
        modifier = Modifier
            .testTag(TestTagAccordionSection)
            .semantics { set(SectionIsSelected, isSelected) }
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
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
                        RenderTextNode(it)
                    }

                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                    )

                    Icon(
                        imageVector = if (isSelected) Icons.Filled.Remove else Icons.Filled.Add,
                        contentDescription = if (isSelected) "Minimize" else "Maximize",
                        modifier = Modifier
                            .size(20.dp, 20.dp)
                    )
                }
            }

            AnimatedVisibility(
                visible = isSelected,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
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
