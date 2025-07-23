package org.cru.godtools.shared.renderer.content

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.onNodeWithTag
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.internal.test.IgnoreOnIos
import org.cru.godtools.shared.tool.parser.model.Video

// TODO: this test is disabled for iOS due to WebView not being supported in the iOS test environment
@IgnoreOnIos
@RunOnAndroidWith(AndroidJUnit4::class)
class RenderVideoTest : BaseRenderContentTest() {
    override val testModel = Video(
        provider = Video.Provider.YOUTUBE,
        videoId = "dQw4w9WgXcQ",
        invisibleIf = invisibleIf,
        goneIf = goneIf
    )
    override fun SemanticsNodeInteractionsProvider.onModelNode() = onNodeWithTag(TestTagVideo)
}
