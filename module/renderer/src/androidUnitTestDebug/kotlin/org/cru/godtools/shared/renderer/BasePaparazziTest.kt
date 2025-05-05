package org.cru.godtools.shared.renderer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
        maxPercentDifference = 0.001,
    )

    protected fun contentSnapshot(content: @Composable ColumnScope.() -> Unit) {
        paparazzi.snapshot {
            Column(modifier = Modifier.background(Color.White), content = content)
        }
    }
}
