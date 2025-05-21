package org.cru.godtools.shared.renderer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams.RenderingMode
import org.junit.Rule

abstract class BasePaparazziTest(renderingMode: RenderingMode = RenderingMode.NORMAL) {
    @get:Rule
    val paparazzi = Paparazzi(
        renderingMode = renderingMode,
        maxPercentDifference = 0.0,
    )

    protected fun contentSnapshot(content: @Composable BoxScope.() -> Unit) {
        paparazzi.snapshot {
            Box(modifier = Modifier.background(Color.White), content = content)
        }
    }
}
