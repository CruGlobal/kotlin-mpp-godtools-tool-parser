package org.cru.godtools.shared.renderer.tract

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import kotlin.test.Test
import kotlinx.coroutines.runBlocking
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Base
import org.cru.godtools.shared.tool.parser.model.Content
import org.cru.godtools.shared.tool.parser.model.Dimension
import org.cru.godtools.shared.tool.parser.model.Gravity
import org.cru.godtools.shared.tool.parser.model.Image
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.tract.Header
import org.cru.godtools.shared.tool.parser.model.tract.Hero
import org.cru.godtools.shared.tool.parser.model.tract.TractPage

class RenderTractHeroPaparazziTest : BasePaparazziTest() {
    private val lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
        "Maecenas eu eros eu nulla dictum rutrum. Donec sollicitudin risus id orci."

    private fun header(
        number: ((Header) -> Text?)? = { Text(it, text = "4") },
        title: ((Header) -> Text?)? = { Text(it, text = "Header") },
        tip: String? = "consider"
    ): (TractPage) -> Header? = { Header(it, number = number, title = title, tip = tip) }

    private fun hero(
        heading: ((Base) -> Text?)? = { Text(it, text = "Hero Heading") },
        content: ((Base) -> List<Content>)? = { listOf(Text(it, text = "Hero Text")) },
    ): (TractPage) -> Hero? = { Hero(it, heading = heading, content = content) }

    @Test
    fun `RenderTractHero() - Full`() = contentSnapshot {
        RenderTractHero(
            TractPage(header = header(), hero = hero()),
            Modifier.fillMaxSize()
        )
    }

    @Test
    fun `RenderTractHero() - Header - Missing`() = contentSnapshot {
        RenderTractHero(
            TractPage(hero = hero()),
            Modifier.fillMaxSize(),
        )
    }

    @Test
    fun `RenderTractHero() - Header - No Number`() = contentSnapshot {
        RenderTractHero(
            TractPage(header = header(number = null), hero = hero()),
            Modifier.fillMaxSize()
        )
    }

    @Test
    fun `RenderTractHero() - Header - Long title`() = contentSnapshot {
        RenderTractHero(
            TractPage(header = header(title = { Text(it, text = lorem) }), hero = hero()),
            Modifier.fillMaxSize()
        )
    }

    @Test
    fun `RenderTractHero() - Header - Tip`() {
        val page = TractPage(manifest, header = header(), hero = hero())
        val state = State().apply { showTips.value = true }

        contentSnapshot {
            RenderTractHero(
                page,
                state = state,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    @Test
    fun `RenderTractHero() - Header - Tip - Completed`() {
        val page = TractPage(manifest, header = header(), hero = hero())
        val state = State().apply { showTips.value = true }
        runBlocking { tipsRepository.markTipComplete(manifest.code!!, manifest.locale!!, "consider") }

        contentSnapshot {
            RenderTractHero(
                page,
                state = state,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    @Test
    fun `RenderTractHero() - Hero - Missing`() = contentSnapshot {
        RenderTractHero(
            TractPage(header = header()),
            Modifier.fillMaxSize(),
        )
    }

    @Test
    fun `RenderTractHero() - Hero - Long heading`() = contentSnapshot {
        RenderTractHero(
            TractPage(hero = hero(heading = { Text(it, text = lorem) })),
            Modifier.fillMaxSize()
        )
    }

    @Test
    fun `RenderTractHero() - No Content`() = contentSnapshot {
        RenderTractHero(
            TractPage(),
            Modifier.fillMaxSize()
        )
    }

    @Test
    fun `RenderTractHero() - GT-2872 - Image Alignment`() {
        val page = TractPage(
            manifest,
            header = null,
            hero = hero {
                listOf(
                    Image(
                        it,
                        resource = "bruce",
                        width = Dimension.Pixels(200),
                        gravity = Gravity.Horizontal.START,
                    ),
                    Image(
                        it,
                        resource = "bruce",
                        width = Dimension.Pixels(200),
                        gravity = Gravity.Horizontal.CENTER,
                    ),
                    Image(
                        it,
                        resource = "bruce",
                        width = Dimension.Pixels(200),
                        gravity = Gravity.Horizontal.END,
                    ),
                )
            },
        )

        contentSnapshot {
            RenderTractHero(page, Modifier.fillMaxSize())
        }
    }
}
