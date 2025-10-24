package org.cru.godtools.shared.renderer.content

import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Dimension
import org.cru.godtools.shared.tool.parser.model.Gravity
import org.cru.godtools.shared.tool.parser.model.Image
import org.cru.godtools.shared.tool.parser.model.Multiselect
import org.cru.godtools.shared.tool.parser.model.Multiselect.Option
import org.cru.godtools.shared.tool.parser.model.Text

class RenderMultiselectPaparazziTest : BasePaparazziTest() {
    private val state = State().apply {
        setVar("multiselect", listOf("1", "4", "6"))
    }

    @Test
    fun `RenderMultiselect - Columns=1 - Options=Card`() = contentSnapshot {
        RenderContentStack(listOf(mockMultiselect(columns = 1, optionStyle = Option.Style.CARD)))
    }

    @Test
    fun `RenderMultiselect - Columns=2 - Options=Card`() = contentSnapshot {
        RenderContentStack(listOf(mockMultiselect(columns = 2, optionStyle = Option.Style.CARD)))
    }

    @Test
    fun `RenderMultiselect - Columns=3 - Options=Mixed`() = contentSnapshot {
        RenderContentStack(listOf(mockMultiselect(columns = 3)))
    }

    @Test
    fun `RenderMultiselect - Columns=4 - Options=Flat`() = contentSnapshot {
        RenderContentStack(listOf(mockMultiselect(columns = 4, optionStyle = Option.Style.FLAT)))
    }

    @Test
    fun `RenderMultiselect - Options=Card - Selected`() = contentSnapshot {
        RenderContentStack(
            listOf(mockMultiselect(columns = 1, optionStyle = Option.Style.CARD)),
            state = state,
        )
    }

    @Test
    fun `RenderMultiselect - Options=Flat - Selected`() = contentSnapshot {
        RenderContentStack(
            listOf(mockMultiselect(columns = 4, optionStyle = Option.Style.FLAT)),
            state = state,
        )
    }

    private fun mockMultiselect(columns: Int, optionStyle: Option.Style? = null) = Multiselect(
        manifest,
        stateName = "multiselect",
        columns = columns,
        selectionLimit = 4,
    ) { multiselect ->
        List(7) { i ->
            Option(
                multiselect,
                style = optionStyle ?: Option.Style.entries[i % 2],
                value = "${i + 1}",
            ) {
                listOf(
                    Text(it, "Option ${i + 1}"),
                    Image(
                        it,
                        resource = "black_panther",
                        width = Dimension.Pixels(50),
                        gravity = Gravity.Horizontal.CENTER,
                    ),
                )
            }
        }
    }
}
