@file:Suppress("ktlint:standard:filename")

package org.cru.godtools.shared.renderer.content.extensions

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import org.cru.godtools.shared.renderer.generated.resources.Res
import org.cru.godtools.shared.renderer.generated.resources.ic_tips_ask
import org.cru.godtools.shared.renderer.generated.resources.ic_tips_ask_done
import org.cru.godtools.shared.renderer.generated.resources.ic_tips_consider
import org.cru.godtools.shared.renderer.generated.resources.ic_tips_consider_done
import org.cru.godtools.shared.renderer.generated.resources.ic_tips_prepare
import org.cru.godtools.shared.renderer.generated.resources.ic_tips_prepare_done
import org.cru.godtools.shared.renderer.generated.resources.ic_tips_quote
import org.cru.godtools.shared.renderer.generated.resources.ic_tips_quote_done
import org.cru.godtools.shared.renderer.generated.resources.ic_tips_tip
import org.cru.godtools.shared.renderer.generated.resources.ic_tips_tip_done
import org.cru.godtools.shared.tool.parser.model.tips.Tip
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun painterTip(tip: Tip?, isComplete: Boolean) = painterResource(
    when (tip?.type) {
        Tip.Type.ASK -> if (isComplete) Res.drawable.ic_tips_ask_done else Res.drawable.ic_tips_ask
        Tip.Type.CONSIDER -> if (isComplete) Res.drawable.ic_tips_consider_done else Res.drawable.ic_tips_consider
        Tip.Type.QUOTE -> if (isComplete) Res.drawable.ic_tips_quote_done else Res.drawable.ic_tips_quote
        Tip.Type.PREPARE -> if (isComplete) Res.drawable.ic_tips_prepare_done else Res.drawable.ic_tips_prepare
        Tip.Type.TIP, null -> if (isComplete) Res.drawable.ic_tips_tip_done else Res.drawable.ic_tips_tip
    }
)

internal fun Modifier.tipBackground(isComplete: Boolean) = when (isComplete) {
    true -> this.background(Brush.radialGradient(listOf(Color.White, Color(red = 0xFF, green = 0xDE, blue = 0xE6))))
    false -> this.background(Color.White)
}
