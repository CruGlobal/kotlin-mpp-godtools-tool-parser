package org.cru.godtools.shared.renderer.tract

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.cru.godtools.shared.renderer.ToolTheme
import org.cru.godtools.shared.renderer.ToolTheme.ContentHorizontalPadding
import org.cru.godtools.shared.renderer.content.RenderContent
import org.cru.godtools.shared.renderer.content.RenderTextNode
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.renderer.tips.TipArrowHeight
import org.cru.godtools.shared.renderer.tips.TipUpArrow
import org.cru.godtools.shared.tool.parser.model.tract.TractPage
import org.cru.godtools.shared.tool.parser.model.tract.backgroundColor

private val HeaderContentVerticalPadding = 16.dp
private val HeaderTipArrowShadowPadding = 4.dp

internal const val TestTagHeader = "header"
internal const val TestTagHeaderNumber = "header_number"
internal const val TestTagHeaderTitle = "header_title"
internal const val TestTagHeroHeading = "hero_heading"

@Composable
fun RenderTractHero(page: TractPage, modifier: Modifier = Modifier, state: State = remember { State() }) {
    Column(modifier = modifier) {
        page.header?.let { header ->
            ProvideTextStyle(MaterialTheme.typography.titleMedium) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(ContentHorizontalPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(header.backgroundColor.toComposeColor())
                        .padding(horizontal = ContentHorizontalPadding)
                        .testTag(TestTagHeader)
                ) {
                    header.number?.let { number ->
                        ProvideTextStyle(LocalTextStyle.current.copy(fontSize = LocalTextStyle.current.fontSize * 3)) {
                            RenderTextNode(number, Modifier.testTag(TestTagHeaderNumber))
                        }
                    }
                    header.title?.let { title ->
                        RenderTextNode(
                            title,
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = HeaderContentVerticalPadding)
                                .testTag(TestTagHeaderTitle)
                        )
                    }

                    header.tip?.let { tip ->
                        if (state.showTips.collectAsState().value) {
                            Popup(
                                alignment = Alignment.BottomStart,
                                offset = with(LocalDensity.current) {
                                    IntOffset(
                                        x = -HeaderTipArrowShadowPadding.roundToPx(),
                                        y = (
                                            (TipArrowHeight + (HeaderTipArrowShadowPadding * 2)) -
                                                HeaderContentVerticalPadding
                                            ).roundToPx(),
                                    )
                                },
                                properties = PopupProperties(clippingEnabled = false),
                            ) {
                                TipUpArrow(tip, state, modifier = Modifier.padding(HeaderTipArrowShadowPadding))
                            }
                        }
                    }
                }
            }
        }
        page.hero?.let { hero ->
            Column(Modifier.padding(horizontal = 16.dp)) {
                hero.heading?.let { heading ->
                    ProvideTextStyle(MaterialTheme.typography.headlineMedium) {
                        RenderTextNode(
                            heading,
                            modifier = Modifier
                                .padding(top = 24.dp)
                                .padding(horizontal = ContentHorizontalPadding)
                                .testTag(TestTagHeroHeading)
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                ProvideTextStyle(ToolTheme.ContentTextStyle) {
                    RenderContent(hero.content, state)
                }
            }
        }
    }
}
