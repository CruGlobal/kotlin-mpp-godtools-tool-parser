package org.cru.godtools.shared.renderer.content

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.onNodeWithTag
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.DelicateCoilApi
import kotlin.test.BeforeTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.TestResources
import org.cru.godtools.shared.renderer.internal.coil.TestPlatformContext
import org.cru.godtools.shared.tool.parser.model.Image
import org.cru.godtools.shared.tool.parser.model.Manifest

@RunOnAndroidWith(AndroidJUnit4::class)
class RenderImageTest : BaseRenderContentTest() {
    override val testModel = Image(
        Manifest(resources = { TestResources.resources }),
        resource = "black_square",
        events = clickableEvents,
        url = clickableUrl,
        invisibleIf = invisibleIf,
        goneIf = goneIf,
    )

    override fun SemanticsNodeInteractionsProvider.onModelNode() = onNodeWithTag(TestTagImage)

    @BeforeTest
    @OptIn(DelicateCoilApi::class)
    fun setupCoil() {
        SingletonImageLoader.setUnsafe(
            ImageLoader.Builder(TestPlatformContext)
                .components { add(TestResources.coilEngine) }
                .build()
        )
    }
}
