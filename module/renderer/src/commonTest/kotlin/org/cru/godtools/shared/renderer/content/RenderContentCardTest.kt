package org.cru.godtools.shared.renderer.content

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.onNodeWithText
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.model.Card
import org.cru.godtools.shared.tool.parser.model.Text

@RunOnAndroidWith(AndroidJUnit4::class)
class RenderContentCardTest : BaseRenderContentTest() {
    override val testModel = Card(
        events = clickableEvents,
        url = clickableUrl,
        invisibleIf = invisibleIf,
        goneIf = goneIf,
        content = listOf(Text(text = "Test"))
    )
    override fun SemanticsNodeInteractionsProvider.onModelNode() = onNodeWithText("Test")
}
