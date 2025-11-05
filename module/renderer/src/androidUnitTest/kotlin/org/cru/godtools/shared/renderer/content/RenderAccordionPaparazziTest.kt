package org.cru.godtools.shared.renderer.content

import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Accordion
import org.cru.godtools.shared.tool.parser.model.Dimension
import org.cru.godtools.shared.tool.parser.model.Gravity
import org.cru.godtools.shared.tool.parser.model.Image
import org.cru.godtools.shared.tool.parser.model.Text

class RenderAccordionPaparazziTest : BasePaparazziTest() {
    private val accordion = Accordion(manifest, invisibleIf = "isSet(a)") {
        listOf(
            Accordion.Section(it, header = { Text(it, "Header 1") }) {
                listOf(
                    Text(
                        it,
                        text = "Section 1 content - At vero eos et accusamus et iusto odio dignissimos ducimus",
                    ),
                )
            },
            Accordion.Section(it, header = { Text(it, "Header 2") }) {
                listOf(
                    Text(
                        it,
                        text = "Section 2 content - Nam libero tempore, cum soluta nobis est eligendi optio cumque",
                    ),
                    Image(
                        it,
                        resource = "bruce",
                        width = Dimension.Percent(0.5f),
                        gravity = Gravity.Horizontal.CENTER,
                    )
                )
            },
            Accordion.Section(it, header = { Text(it, "Header 3") }) {
                listOf(
                    Text(
                        parent = it,
                        text = "Section 3 content - Ut enim ad minima veniam, quis nostrum exercitationem ullam",
                    ),
                )
            },
        )
    }

    @Test
    fun `RenderAccordion() - No Sections Expanded`() = contentSnapshot {
        RenderContentStack(listOf(accordion))
    }

    @Test
    fun `RenderAccordion() - Section 2 Expanded`() = contentSnapshot {
        val state = State().apply {
            toggleAccordionSection(accordion.sections[1])
        }
        RenderContentStack(listOf(accordion), state = state)
    }

    @Test
    fun `RenderAccordion() - IsInvisible`() {
        val state = State().apply {
            setVar("a", listOf("value"))
        }

        contentSnapshot {
            RenderContentStack(
                listOf(
                    Text(text = "Before"),
                    accordion,
                    Text(text = "After")
                ),
                state = state
            )
        }
    }
}
