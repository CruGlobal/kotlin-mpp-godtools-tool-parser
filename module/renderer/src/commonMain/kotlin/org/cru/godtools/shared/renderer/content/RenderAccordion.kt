package org.cru.godtools.shared.renderer.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.cru.godtools.shared.renderer.ToolTheme
import org.cru.godtools.shared.renderer.ToolTheme.CardPadding
import org.cru.godtools.shared.renderer.ToolTheme.ContentHorizontalPadding
import org.cru.godtools.shared.renderer.content.extensions.visibility
import org.cru.godtools.shared.renderer.generated.resources.Res
import org.cru.godtools.shared.renderer.generated.resources.tool_renderer_accordion_section_action_collapse
import org.cru.godtools.shared.renderer.generated.resources.tool_renderer_accordion_section_action_expand
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Accordion
import org.jetbrains.compose.resources.stringResource

internal const val TestTagAccordion = "accordion"
internal const val TestTagAccordionSection = "accordion section"

@Composable
fun RenderAccordion(accordion: Accordion, state: State, modifier: Modifier = Modifier) {
    val accordionId = accordion.id
    val expandedSections by remember(state, accordionId) { state.accordionExpandedSectionsFlow(accordionId) }
        .collectAsState(state.accordionExpandedSections(accordionId))

    Column(
        modifier = modifier
            .testTag(TestTagAccordion)
            .visibility(accordion, state)
    ) {
        accordion.sections.forEachIndexed { index, section ->
            val sectionId = section.id
            key(sectionId) {
                RenderAccordionSection(
                    section,
                    state = state,
                    isExpanded = sectionId in expandedSections,
                    onClick = { state.toggleAccordionSection(section) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(CardPadding)
                )
            }
        }
    }
}

@Composable
private fun RenderAccordionSection(
    section: Accordion.Section,
    state: State,
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardColor = section.backgroundColor.toComposeColor()

    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        elevation = ToolTheme.cardElevation(),
        modifier = modifier
            .testTag(TestTagAccordionSection)
            .semantics { selected = isExpanded }
    ) {
        val headerInteractions = remember { MutableInteractionSource() }

        Row(
            modifier = Modifier
                .clickable(interactionSource = headerInteractions, indication = null, onClick = onClick)
                .heightIn(min = 48.dp)
        ) {
            section.header?.let {
                RenderTextNode(
                    it,
                    modifier = Modifier
                        .padding(start = ContentHorizontalPadding)
                        .padding(vertical = 4.dp)
                        .align(alignment = Alignment.CenterVertically)
                )
            }

            Spacer(Modifier.weight(1f))

            Icon(
                imageVector = when {
                    isExpanded -> Icons.Filled.Remove
                    else -> Icons.Filled.Add
                },
                contentDescription = when {
                    isExpanded -> stringResource(Res.string.tool_renderer_accordion_section_action_collapse)
                    else -> stringResource(Res.string.tool_renderer_accordion_section_action_expand)
                },
                modifier = Modifier
                    .indication(headerInteractions, ripple(bounded = false, radius = 20.dp))
                    .padding(12.dp)
                    .align(Alignment.Top)
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
            ) {
                RenderContent(section.content, state)
            }
        }
    }
}
