package org.cru.godtools.shared.renderer.content

import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.tool.parser.model.Dimension
import org.cru.godtools.shared.tool.parser.model.Gravity
import org.cru.godtools.shared.tool.parser.model.Image
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
                        Tabs.Tab(
                            label = Text(text = "1"),
                            content = listOf(
                                Spacer(),
                                Text(
                                    text = "Tab 1 Content",
                                    textAlign = Text.Align.CENTER
                                ),
                                Image(
                                    manifest,
                                    resource = "bruce",
                                    width = Dimension.Percent(0.5f),
                                    gravity = Gravity.Horizontal.CENTER,
                                ),
                                Spacer()
                            )
                        ),
                        Tabs.Tab(
                            label = Text(text = "2"),
                            content = listOf(
                                Spacer(),
                                Text(
                                    text = "Tab 2 Content",
                                    textAlign = Text.Align.CENTER
                                ),
                                Image(
                                    manifest,
                                    resource = "bruce",
                                    width = Dimension.Percent(0.5f),
                                    gravity = Gravity.Horizontal.CENTER,
                                ),
                                Spacer()
                            )
                        )
                    )
                ),
                Spacer(),
                Text(text = "Render Tabs")
            )
        )
    }
}
