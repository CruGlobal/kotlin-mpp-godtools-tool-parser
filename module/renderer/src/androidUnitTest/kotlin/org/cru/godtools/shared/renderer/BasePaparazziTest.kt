package org.cru.godtools.shared.renderer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.accessibility.AccessibilityRenderExtension
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.DelicateCoilApi
import com.android.ide.common.rendering.api.SessionParams.RenderingMode
import kotlin.test.BeforeTest
import kotlin.uuid.ExperimentalUuidApi
import okio.Path.Companion.toPath
import org.cru.godtools.shared.renderer.util.ProvideRendererServices
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Resource
import org.junit.Rule

@OptIn(ExperimentalUuidApi::class)
abstract class BasePaparazziTest(
    protected val accessibilityMode: AccessibilityMode = AccessibilityMode.NO_ACCESSIBILITY,
    renderingMode: RenderingMode = RenderingMode.NORMAL
) {
    enum class AccessibilityMode { ACCESSIBILITY, NO_ACCESSIBILITY }

    @get:Rule
    val paparazzi = Paparazzi(
        renderingMode = renderingMode,
        maxPercentDifference = 0.0,
        renderExtensions = buildSet {
            if (accessibilityMode == AccessibilityMode.ACCESSIBILITY) add(AccessibilityRenderExtension())
        }
    )

    protected val manifest = Manifest(
        resources = { TestResources.resources + Resource(name = "bruce", localName = "bruce.jpg") },
    )

    @BeforeTest
    @OptIn(DelicateCoilApi::class)
    fun setupCoil() {
        val engine = TestResources.coilEngine.newBuilder()
            .intercept("bruce.jpg".toPath(), CoilTestImages.bruce)
            .build()

        SingletonImageLoader.setUnsafe(
            ImageLoader.Builder(paparazzi.context)
                .components { add(engine) }
                .build(),
        )
    }

    protected fun contentSnapshot(content: @Composable BoxScope.() -> Unit) {
        paparazzi.snapshot {
            ProvideRendererServices(TestResources.fileSystem) {
                Box(modifier = Modifier.background(Color.White), content = content)
            }
        }
    }
}
