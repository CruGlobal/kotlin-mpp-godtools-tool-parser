package org.cru.godtools.shared.renderer.content

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.onNodeWithText
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.model.Button
import org.cru.godtools.shared.tool.parser.model.Tabs
import org.cru.godtools.shared.tool.parser.model.Text

@RunOnAndroidWith(AndroidJUnit4::class)
class RenderTabsTest : BaseRenderContentTest() {
    override val testModel = Tabs(
        tabs = listOf(
            Tabs.Tab(label = null),
            Tabs.Tab(label = null)
        ),
        invisibleIf = invisibleIf,
        goneIf = goneIf
    )
    override fun SemanticsNodeInteractionsProvider.onModelNode() = onNodeWithText("Test")
}
