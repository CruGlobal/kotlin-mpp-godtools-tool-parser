package org.cru.godtools.shared.renderer.content

import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.tool.parser.model.Dimension
import org.cru.godtools.shared.tool.parser.model.Dimension.Percent
import org.cru.godtools.shared.tool.parser.model.Flow
import org.cru.godtools.shared.tool.parser.model.Gravity
import org.cru.godtools.shared.tool.parser.model.Image
import org.cru.godtools.shared.tool.parser.model.Text

class RenderFlowPaparazziTest : BasePaparazziTest() {
    @Test
    fun `RenderFlow - Percent Widths`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Flow(manifest) {
                    listOf(
                        Flow.Item(it, width = Percent(0.5f)) { listOf(Image(it, resource = "red_square")) },
                        Flow.Item(it, width = Percent(0.5f)) { listOf(Image(it, resource = "green_square")) },

                        Flow.Item(it, width = Percent(0.333f)) { listOf(Image(it, resource = "red_square")) },
                        Flow.Item(it, width = Percent(0.333f)) { listOf(Image(it, resource = "green_square")) },
                        Flow.Item(it, width = Percent(0.333f)) { listOf(Image(it, resource = "black_square")) },

                        Flow.Item(it, width = Percent(0.25f)) { listOf(Image(it, resource = "red_square")) },
                        Flow.Item(it, width = Percent(0.25f)) { listOf(Image(it, resource = "green_square")) },
                        Flow.Item(it, width = Percent(0.25f)) { listOf(Image(it, resource = "black_square")) },
                        Flow.Item(it, width = Percent(0.25f)) { listOf(Image(it, resource = "red_square")) },
                    )
                },
            ),
        )
    }

    @Test
    fun `RenderFlow - Fixed Widths`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Flow(manifest) {
                    listOf(
                        Flow.Item(it, width = Dimension.Pixels(64)) { listOf(Image(it, resource = "red_square")) },
                        Flow.Item(it, width = Dimension.Pixels(96)) { listOf(Image(it, resource = "green_square")) },
                        Flow.Item(it, width = Dimension.Pixels(128)) { listOf(Image(it, resource = "black_square")) },
                        Flow.Item(it, width = Dimension.Pixels(160)) { listOf(Image(it, resource = "red_square")) },
                        Flow.Item(it, width = Dimension.Pixels(192)) { listOf(Image(it, resource = "green_square")) },
                        Flow.Item(it, width = Dimension.Pixels(224)) { listOf(Image(it, resource = "black_square")) },
                    )
                }
            )
        )
    }

    @Test
    fun `RenderFlow - Row Gravity`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Flow(manifest, rowGravity = Gravity.Horizontal.START) {
                    listOf(
                        Flow.Item(it, width = Percent(0.5f)) {
                            listOf(
                                Text(
                                    it,
                                    "Start",
                                    textAlign = Text.Align.CENTER,
                                    startImage = "green_square",
                                    startImageSize = 20,
                                    endImage = "green_square",
                                    endImageSize = 20,
                                ),
                            )
                        },
                    )
                },
                Flow(manifest, rowGravity = Gravity.Horizontal.CENTER) {
                    listOf(
                        Flow.Item(it, width = Percent(0.5f)) {
                            listOf(
                                Text(
                                    it,
                                    "Center",
                                    textAlign = Text.Align.CENTER,
                                    startImage = "red_square",
                                    startImageSize = 20,
                                    endImage = "red_square",
                                    endImageSize = 20,
                                ),
                            )
                        },
                    )
                },
                Flow(manifest, rowGravity = Gravity.Horizontal.END) {
                    listOf(
                        Flow.Item(it, width = Percent(0.5f)) {
                            listOf(
                                Text(
                                    it,
                                    "End",
                                    textAlign = Text.Align.CENTER,
                                    startImage = "green_square",
                                    startImageSize = 20,
                                    endImage = "green_square",
                                    endImageSize = 20,
                                ),
                            )
                        },
                    )
                },
            ),
        )
    }
}
