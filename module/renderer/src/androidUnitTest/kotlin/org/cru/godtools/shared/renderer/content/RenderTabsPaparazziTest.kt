package org.cru.godtools.shared.renderer.content

import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.tool.parser.model.Spacer
import org.cru.godtools.shared.tool.parser.model.Tabs
import org.cru.godtools.shared.tool.parser.model.Text

class RenderTabsPaparazziTest : BasePaparazziTest() {

    @Test
    fun `RenderTabs() - Tabs`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Text(text = "Render Tabs"),
                Spacer(),
                Tabs(
                    tabs = listOf(
                        Tabs.Tab(label = Text(text = "Tab 1")),
                        Tabs.Tab(label = Text(text = "Tab 2"))
                    ),
                ),
                Spacer(),
                Text(text = "Render Tabs")
            )
        )
    }
}
