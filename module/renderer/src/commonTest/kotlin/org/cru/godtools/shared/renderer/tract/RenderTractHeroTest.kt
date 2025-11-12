package org.cru.godtools.shared.renderer.tract

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.BaseRendererTest
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.tract.Header
import org.cru.godtools.shared.tool.parser.model.tract.Hero
import org.cru.godtools.shared.tool.parser.model.tract.TractPage

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class RenderTractHeroTest : BaseRendererTest() {
    private fun SemanticsNodeInteractionsProvider.onHeaderNode() = onNodeWithTag(TestTagHeader)
    private fun SemanticsNodeInteractionsProvider.onHeaderNumberNode() = onNodeWithTag(TestTagHeaderNumber)
    private fun SemanticsNodeInteractionsProvider.onHeaderTitleNode() = onNodeWithTag(TestTagHeaderTitle)
    private fun SemanticsNodeInteractionsProvider.onHeroHeadingNode() = onNodeWithTag(TestTagHeroHeading)

    @Test
    fun `UI - Header - Number`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderTractHero(TractPage(header = { Header(number = { Text(text = "1") }) }))
            }
        }

        onHeaderNode().assertExists()
        onHeaderNumberNode().assertExists()
    }

    @Test
    fun `UI - Header - Number - Not Present`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderTractHero(TractPage(header = { Header(number = null) }))
            }
        }

        onHeaderNode().assertExists()
        onHeaderNumberNode().assertDoesNotExist()
    }

    @Test
    fun `UI - Header - Title`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderTractHero(TractPage(header = { Header(title = { Text(text = "title") }) }))
            }
        }

        onHeaderNode().assertExists()
        onHeaderTitleNode().assertExists()
    }

    @Test
    fun `UI - Header - Title - Not Present`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderTractHero(TractPage(header = { Header(title = null) }))
            }
        }

        onHeaderNode().assertExists()
        onHeaderTitleNode().assertDoesNotExist()
    }

    @Test
    fun `UI - Header - Not Present`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderTractHero(TractPage(header = null))
            }
        }

        onHeaderNode().assertDoesNotExist()
    }

    @Test
    fun `UI - Hero - Heading`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderTractHero(TractPage(hero = { Hero(heading = { Text(text = "heading") }) }))
            }
        }

        onHeroHeadingNode().assertExists()
    }

    @Test
    fun `UI - Hero - Heading - Not Present`() = runComposeUiTest {
        setContent {
            ProvideTestCompositionLocals {
                RenderTractHero(TractPage(hero = { Hero(heading = null) }))
            }
        }

        onHeroHeadingNode().assertDoesNotExist()
    }
}
