package org.cru.godtools.shared.renderer.content

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.onNodeWithTag
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.TestResources
import org.cru.godtools.shared.tool.parser.model.Animation
import org.cru.godtools.shared.tool.parser.model.Manifest

@RunOnAndroidWith(AndroidJUnit4::class)
class RenderAnimationTest : BaseRenderContentTest() {
    private val manifest = Manifest(resources = { TestResources.resources })

    override val testModel = Animation(
        manifest,
        "kotlin_anim",
        invisibleIf = invisibleIf,
        goneIf = goneIf,
        events = clickableEvents,
        url = clickableUrl,
    )

    override fun SemanticsNodeInteractionsProvider.onModelNode() = onNodeWithTag(TEST_TAG_ANIMATION)
}
