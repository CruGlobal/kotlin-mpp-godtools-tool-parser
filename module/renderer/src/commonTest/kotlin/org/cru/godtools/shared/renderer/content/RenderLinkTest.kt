package org.cru.godtools.shared.renderer.content

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.onNodeWithText
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.model.Link
import org.cru.godtools.shared.tool.parser.model.Text

@RunOnAndroidWith(AndroidJUnit4::class)
class RenderLinkTest : BaseRenderContentTest() {
    override val testModel = Link(
        text = { Text(text = "Test") },
        events = clickableEvents,
        url = clickableUrl,
        invisibleIf = invisibleIf,
        goneIf = goneIf
    )
    override fun SemanticsNodeInteractionsProvider.onModelNode() = onNodeWithText("Test")
}
