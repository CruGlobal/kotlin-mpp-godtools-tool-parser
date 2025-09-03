package org.cru.godtools.shared.renderer

import android.view.ViewGroup.LayoutParams
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalInspectionMode
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.accessibility.AccessibilityRenderExtension
import app.cash.paparazzi.detectEnvironment
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.DelicateCoilApi
import com.android.ide.common.rendering.api.SessionParams.RenderingMode
import io.fluidsonic.locale.Locale
import kotlin.test.BeforeTest
import kotlin.uuid.ExperimentalUuidApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import okio.Path.Companion.toPath
import org.cru.godtools.shared.renderer.content.LocalCompottieCoroutineContext
import org.cru.godtools.shared.renderer.tips.InMemoryTipsRepository
import org.cru.godtools.shared.renderer.util.ProvideRendererServices
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Resource
import org.cru.godtools.shared.tool.parser.model.tips.Tip
import org.jetbrains.compose.resources.PreviewContextConfigurationEffect
import org.junit.Rule

@OptIn(ExperimentalUuidApi::class)
abstract class BasePaparazziTest(
    protected val accessibilityMode: AccessibilityMode = AccessibilityMode.NO_ACCESSIBILITY,
    renderingMode: RenderingMode = RenderingMode.NORMAL
) {
    protected companion object {
        val resourceBlackPanther = Resource(name = "black_panther", localName = "black_panther.png")
        val resourceBruce = Resource(name = "bruce", localName = "bruce.jpg")
        val resourceWaterfall = Resource(name = "waterfall", localName = "waterfall.jpg")
    }
    enum class AccessibilityMode { ACCESSIBILITY, NO_ACCESSIBILITY }

    @get:Rule
    val paparazzi = Paparazzi(
        environment = detectEnvironment().copy(
            // HACK: Override the compileSdkVersion to 35 to workaround Paparazzi not yet supporting SDK 36
            //       https://github.com/cashapp/paparazzi/pull/2066 might solve the issue
            compileSdkVersion = 35,
        ),
        renderingMode = renderingMode,
        maxPercentDifference = 0.0,
        renderExtensions = buildSet {
            if (accessibilityMode == AccessibilityMode.ACCESSIBILITY) add(AccessibilityRenderExtension())
        }
    )

    protected val tipsRepository = InMemoryTipsRepository()

    protected val manifest by lazy {
        Manifest(
            code = "tool",
            locale = Locale.forLanguage("en"),
            resources = {
                listOf(
                    resourceBlackPanther,
                    resourceBruce,
                    resourceWaterfall,
                ) + TestResources.resources
            },
            tips = {
                listOf(
                    Tip(it, id = "ask", type = Tip.Type.ASK),
                    Tip(it, id = "consider", type = Tip.Type.CONSIDER),
                    Tip(it, id = "prepare", type = Tip.Type.PREPARE),
                    Tip(it, id = "quote", type = Tip.Type.QUOTE),
                    Tip(it, id = "tip", type = Tip.Type.TIP),
                )
            },
        )
    }

    @BeforeTest
    @OptIn(DelicateCoilApi::class)
    fun setupCoil() {
        val engine = TestResources.coilEngine.newBuilder()
            .intercept("bruce.jpg".toPath(), CoilTestImages.bruce)
            .intercept("black_panther.png".toPath(), CoilTestImages.blackPanther)
            .intercept("waterfall.jpg".toPath(), CoilTestImages.waterfall)
            .build()

        SingletonImageLoader.setUnsafe(
            ImageLoader.Builder(paparazzi.context)
                .components { add(engine) }
                .build(),
        )
    }

    protected fun contentSnapshot(name: String? = null, content: @Composable BoxScope.() -> Unit) {
        paparazzi.snapshot(name) {
            val coroutineScope = rememberCoroutineScope()

            CompositionLocalProvider(LocalInspectionMode provides true) {
                PreviewContextConfigurationEffect()
            }
            CompositionLocalProvider(LocalCompottieCoroutineContext provides coroutineScope.coroutineContext) {
                ProvideRendererServices(
                    resources = TestResources.fileSystem,
                    tipsRepository = tipsRepository,
                ) {
                    Box(modifier = Modifier.background(Color.White), content = content)
                }
            }
        }
    }

    protected fun animatedContentSnapshot(
        start: Long = 0L,
        end: Long = 500L,
        content: @Composable BoxScope.() -> Unit,
    ) {
        paparazzi.gif(start = start, end = end) {
            val coroutineScope = rememberCoroutineScope()

            CompositionLocalProvider(LocalInspectionMode provides true) {
                PreviewContextConfigurationEffect()
            }
            CompositionLocalProvider(LocalCompottieCoroutineContext provides coroutineScope.coroutineContext) {
                ProvideRendererServices(TestResources.fileSystem) {
                    Box(modifier = Modifier.background(Color.White), content = content)
                }
            }
        }
    }
}

private fun Paparazzi.gif(
    name: String? = null,
    start: Long = 0L,
    end: Long = 500L,
    fps: Int = 30,
    composable: @Composable () -> Unit,
) {
    val hostView = ComposeView(context).apply {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }
    hostView.setContent(composable)

    gif(hostView, name, start, end, fps)
}
