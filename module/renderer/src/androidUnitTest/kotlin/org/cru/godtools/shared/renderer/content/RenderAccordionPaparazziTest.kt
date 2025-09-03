package org.cru.godtools.shared.renderer.content

import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.tool.parser.model.Accordion

class RenderAccordionPaparazziTest : BasePaparazziTest() {

    @Test
    fun `RenderAccordion() - Accordion With 3 Sections`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Accordion {
                    listOf(
                        Accordion.Section(it),
                        Accordion.Section(it),
                        Accordion.Section(it)
                    )
                }
            )
        )
    }
}
