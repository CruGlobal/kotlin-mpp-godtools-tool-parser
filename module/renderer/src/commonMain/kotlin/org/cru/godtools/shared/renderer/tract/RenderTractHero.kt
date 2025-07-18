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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.cru.godtools.shared.renderer.content.HorizontalPadding
import org.cru.godtools.shared.renderer.content.RenderContent
import org.cru.godtools.shared.renderer.content.RenderTextNode
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.tract.TractPage
import org.cru.godtools.shared.tool.parser.model.tract.backgroundColor

@Composable
fun RenderTractHero(page: TractPage, modifier: Modifier = Modifier, state: State = remember { State() }) {
    Column(modifier = modifier) {
        page.header?.let { header ->
            ProvideTextStyle(MaterialTheme.typography.titleMedium) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(HorizontalPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(header.backgroundColor.toComposeColor())
                        .padding(horizontal = HorizontalPadding)
                ) {
                    header.number?.let { number ->
                        ProvideTextStyle(LocalTextStyle.current.copy(fontSize = LocalTextStyle.current.fontSize * 3)) {
                            RenderTextNode(number)
                        }
                    }
                    header.title?.let { title ->
                        RenderTextNode(
                            title,
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 16.dp)
                        )
                    }
                }
            }
            // TODO: Render header tips
        }
        page.hero?.let { hero ->
            hero.heading?.let { heading ->
                ProvideTextStyle(MaterialTheme.typography.headlineMedium) {
                    RenderTextNode(
                        heading,
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .padding(horizontal = HorizontalPadding)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            RenderContent(hero.content, state)
        }
    }
}
