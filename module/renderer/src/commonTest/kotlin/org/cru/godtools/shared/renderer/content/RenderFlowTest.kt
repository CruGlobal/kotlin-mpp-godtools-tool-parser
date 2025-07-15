package org.cru.godtools.shared.renderer.content

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.onNodeWithTag
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.model.Flow
import org.cru.godtools.shared.tool.parser.model.Text

@RunOnAndroidWith(AndroidJUnit4::class)
class RenderFlowTest : BaseRenderContentTest() {
    override val testModel = Flow(goneIf = goneIf, invisibleIf = invisibleIf) {
        listOf(
            Flow.Item(it) {
                listOf(
                    Text(it, text = "Test")
                )
            }
        )
    }

    override fun SemanticsNodeInteractionsProvider.onModelNode() = onNodeWithTag(TEST_TAG_FLOW)
}
