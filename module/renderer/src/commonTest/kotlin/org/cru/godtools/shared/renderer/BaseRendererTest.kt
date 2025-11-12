package org.cru.godtools.shared.renderer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.testing.TestLifecycleOwner
import kotlin.test.BeforeTest
import kotlinx.coroutines.test.TestScope
import org.cru.godtools.shared.renderer.internal.compose.resources.PreviewContextConfigurationEffect
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.renderer.util.ProvideRendererServices

abstract class BaseRendererTest {
    protected val state = State()

    protected val lifecycleOwner = TestLifecycleOwner(Lifecycle.State.RESUMED)
    protected val testScope = TestScope()

    @Composable
    protected fun ProvideTestCompositionLocals(content: @Composable () -> Unit) {
        CompositionLocalProvider(LocalInspectionMode provides true) {
            PreviewContextConfigurationEffect()
        }
        ProvideRendererServices(TestResources.fileSystem) {
            CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner, content = content)
        }
    }

    @BeforeTest
    fun setup() {
        state.setTestCoroutineScope(testScope.backgroundScope)
    }
}
