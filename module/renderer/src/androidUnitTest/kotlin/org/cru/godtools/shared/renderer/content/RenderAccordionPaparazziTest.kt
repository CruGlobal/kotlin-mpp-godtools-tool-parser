package org.cru.godtools.shared.renderer.content

import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.tool.parser.model.Accordion

class RenderAccordionPaparazziTest : BasePaparazziTest() {

    @Test
    fun `RenderAccordion() - Accordion With No Selections`() = contentSnapshot {
        RenderContentStack(
            listOf(
                Accordion()
            )
        )
    }
}
