package org.cru.godtools.shared.renderer.content

import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.tool.parser.model.Accordion
import org.cru.godtools.shared.tool.parser.model.Dimension
import org.cru.godtools.shared.tool.parser.model.Gravity
import org.cru.godtools.shared.tool.parser.model.Image
import org.cru.godtools.shared.tool.parser.model.Text

class RenderAccordionPaparazziTest : BasePaparazziTest() {

    @Test
    fun `RenderAccordion() - Accordion With 3 Sections`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Accordion {
                    listOf(
                        Accordion.Section(it, header = Text(it, "Header 1")) {
                            listOf(
                                Text(
                                    parent = it,
                                    text = "Section 1 content - At vero eos et accusamus et iusto odio dignissimos ducimus",
                                    textAlign = Text.Align.CENTER
                                ),
                                Image(
                                    parent = it,
                                    resource = "bruce",
                                    width = Dimension.Percent(0.5f),
                                    gravity = Gravity.Horizontal.CENTER,
                                )
                            )
                        },
                        Accordion.Section(it, header = Text(it, "Header 2")) {
                            listOf(
                                Text(
                                    parent = it,
                                    text = "Section 2 content - Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda est, omnis dolor repellendus.",
                                    textAlign = Text.Align.CENTER
                                ),
                                Image(
                                    parent = it,
                                    resource = "bruce",
                                    width = Dimension.Percent(0.5f),
                                    gravity = Gravity.Horizontal.CENTER,
                                )
                            )
                        },
                        Accordion.Section(it, header = Text(it, "Header 3")) {
                            listOf(
                                Text(
                                    parent = it,
                                    text = "Section 3 content - Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam",
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
                }
            )
        )
    }
}
