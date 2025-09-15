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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.cru.godtools.shared.renderer.content.extensions.visibility
import org.cru.godtools.shared.renderer.generated.resources.Res
import org.cru.godtools.shared.renderer.generated.resources.accordion_section_action_collapse
import org.cru.godtools.shared.renderer.generated.resources.accordion_section_action_expand
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Accordion
import org.cru.godtools.shared.tool.parser.model.cardBackgroundColor
import org.cru.godtools.shared.tool.parser.model.stylesParent
import org.jetbrains.compose.resources.stringResource

internal const val TestTagAccordion = "accordion"
internal const val TestTagAccordionSection = "accordion section"

@Composable
fun RenderAccordion(
    accordion: Accordion,
    state: State,
    modifier: Modifier = Modifier,
    supportsMultiSelection: Boolean = false
) {
    val selectedSections = remember { mutableStateSetOf<String>() }

    Column(
        modifier = modifier
            .testTag(TestTagAccordion)
            .visibility(accordion, state)
    ) {
        accordion.sections.forEachIndexed { index, section ->
            key(section.id) {
                val isSelected = selectedSections.contains(section.id)

                RenderAccordionSection(
                    section,
                    state = state,
                    isSelected = isSelected,
                    onClick = {
                        if (isSelected) {
                            selectedSections.remove(section.id)
                        } else if (supportsMultiSelection) {
                            selectedSections.add(section.id)
                        } else {
                            selectedSections.clear()
                            selectedSections.add(section.id)
                        }
                    },
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
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardColor = section.stylesParent.cardBackgroundColor.toComposeColor()

    ElevatedCard(
        shape = CardDefaults.elevatedShape,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        modifier = modifier
            .testTag(TestTagAccordionSection)
            .semantics { selected = isSelected }
    ) {
        val headerInteractions = remember { MutableInteractionSource() }

        Row(
            modifier = Modifier
                .clickable(interactionSource = headerInteractions, indication = null) {
                    onClick()
                }
                .heightIn(48.dp)
        ) {
            section.header?.let {
                RenderTextNode(
                    it,
                    modifier = Modifier
                        .padding(start = HorizontalPadding)
                        .align(alignment = Alignment.CenterVertically)
                )
            }

            Spacer(
                modifier = Modifier
                    .weight(1f)
            )

            Icon(
                imageVector = when {
                    isSelected -> Icons.Filled.Remove
                    else -> Icons.Filled.Add
                },
                contentDescription = when {
                    isSelected -> stringResource(Res.string.accordion_section_action_collapse)
                    else -> stringResource(Res.string.accordion_section_action_expand)
                },
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.Top)
                    .indication(headerInteractions, ripple(bounded = false, radius = 20.dp))
            )
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
