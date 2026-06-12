package org.cru.godtools.shared.renderer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.ajalt.colormath.extensions.android.composecolor.toColormathColor
import com.github.ajalt.colormath.model.RGB
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.google.testing.junit.testparameterinjector.TestParameterValuesProvider
import kotlin.test.Test
import org.cru.godtools.shared.tool.parser.model.Background
import org.cru.godtools.shared.tool.parser.model.Gravity
import org.cru.godtools.shared.tool.parser.model.ImageScaleType
import org.cru.godtools.shared.tool.parser.model.Resource
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class RenderBackgroundPaparazziTest : BasePaparazziTest() {
    private object ResourceValuesProvider : TestParameterValuesProvider() {
        override fun provideValues(context: Context?) = listOf(
            value(resourceBruce).withName("bruce"),
            value(resourceWaterfall).withName("waterfall"),
        )
    }

    @Test
    fun `RenderBackground() - Image - Transparency`(
        @TestParameter("#000", "#00f", "#0f0", "#0ff", "#f00", "#f0f", "#ff0", "#fff") color: String,
    ) = contentSnapshot {
        RenderBackground(
            Background(
                RGB(color),
                resourceBlackPanther,
                Gravity.CENTER,
                ImageScaleType.FIT,
            ),
            modifier = Modifier.fillMaxSize(),
        )
    }

    @Test
    fun `RenderBackground() - Image - ScaleType & Gravity`(
        @TestParameter(valuesProvider = ResourceValuesProvider::class) resource: Resource,
        @TestParameter scaleType: ImageScaleType,
        @TestParameter horizontal: Gravity.Horizontal,
        @TestParameter vertical: Gravity.Vertical,
    ) = contentSnapshot {
        RenderBackground(
            Background(
                Color.White.toColormathColor(),
                resource,
                imageScaleType = scaleType,
                imageGravity = Gravity(horizontal, vertical),
            ),
            modifier = Modifier.fillMaxSize(),
        )
    }

    @Test
    fun `RenderBackground() - No Image`(
        @TestParameter("#000", "#00f", "#0f0", "#0ff", "#f00", "#f0f", "#ff0", "#fff") color: String,
    ) = contentSnapshot {
        RenderBackground(
            Background(
                RGB(color),
                image = null,
                imageScaleType = ImageScaleType.FIT,
                imageGravity = Gravity.CENTER,
            ),
            modifier = Modifier.fillMaxSize(),
        )
    }
}
