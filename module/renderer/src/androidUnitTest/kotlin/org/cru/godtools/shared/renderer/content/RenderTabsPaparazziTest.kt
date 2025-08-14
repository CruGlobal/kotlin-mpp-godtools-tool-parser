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
                    parent = manifest,
                    tabs = {
                        listOf(
                            Tabs.Tab(
                                parent = it,
                                label = Text(text = "1"),
                                content = {
                                    listOf(
                                        Text(
                                            parent = it,
                                            text = "Tab 1 Content",
                                            textAlign = Text.Align.CENTER
                                        ),
                                        Image(
                                            parent = it,
                                            resource = "bruce",
                                            width = Dimension.Percent(0.5f),
                                            gravity = Gravity.Horizontal.CENTER,
                                        )
                                    )
                                }
                            ),
                            Tabs.Tab(
                                parent = it,
                                label = Text(text = "2"),
                                content = {
                                    listOf(
                                        Text(
                                            parent = it,
                                            text = "Tab 2 Content",
                                            textAlign = Text.Align.CENTER
                                        ),
                                        Image(
                                            parent = it,
                                            resource = "bruce",
                                            width = Dimension.Percent(0.5f),
                                            gravity = Gravity.Horizontal.CENTER,
                                        )
                                    )
                                }
                            )
                        )
                    }
                ),
                Spacer(),
                Text(text = "Render Tabs")
            )
        )
    }
}
