package org.cru.godtools.shared.renderer.tract

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasText
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import me.saket.touchrobot.onNode
import me.saket.touchrobot.rememberTouchRobot
import org.ccci.gto.android.common.androidx.compose.ui.platform.AndroidUiDispatcherUtil
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.renderer.generated.resources.Res
import org.cru.godtools.shared.renderer.generated.resources.tract_card_action_next
import org.cru.godtools.shared.renderer.generated.resources.tract_card_action_previous
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.navBarColor
import org.cru.godtools.shared.tool.parser.model.tips.InlineTip
import org.cru.godtools.shared.tool.parser.model.tract.CallToAction
import org.cru.godtools.shared.tool.parser.model.tract.Header
import org.cru.godtools.shared.tool.parser.model.tract.Hero
import org.cru.godtools.shared.tool.parser.model.tract.TractPage
import org.jetbrains.compose.resources.getString

private val PseudoAppBarHeight = 56.dp

class RenderTractPagePaparazziTest : BasePaparazziTest() {
    private val state = State()
    private val nextText by lazy { runBlocking { getString(Res.string.tract_card_action_next) } }
    private val previousText by lazy { runBlocking { getString(Res.string.tract_card_action_previous) } }

    private fun testHeader(page: TractPage) =
        Header(page, number = { h -> Text(h, text = "1") }, title = { h -> Text(h, text = "Header Title") })

    private fun testHero(page: TractPage) = Hero(
        page,
        heading = { h -> Text(h, text = "Have you ever wondered what life is really about?") },
        content = { h ->
            listOf(
                Text(h, text = "“For God so loved the world that he gave his one and only Son...”"),
                Text(h, text = "John 3:16"),
            )
        },
    )

    private fun testCta(page: TractPage) =
        CallToAction(page, label = { cta -> Text(cta, text = "Keep going!") }, tip = "ask")

    private fun testPage(
        container: Manifest = manifest,
        backgroundImage: String? = null,
        hiddenCards: Set<Int> = emptySet(),
    ) = TractPage(
        container = container,
        backgroundImage = backgroundImage,
        header = ::testHeader,
        hero = ::testHero,
        cards = { page ->
            List(3) { i ->
                TractPage.Card(
                    page,
                    i,
                    isHidden = i in hiddenCards,
                    label = { p -> Text(p, text = "Card ${i + 1}") },
                    content = { c ->
                        buildList {
                            add(Text(c, text = "Card ${i + 1} content paragraph."))
                            if (i == 1) add(InlineTip(c, id = "tip"))
                        }
                    },
                )
            }
        },
        callToAction = ::testCta,
    )

    private fun testPageWithoutCards(backgroundImage: String? = null) = TractPage(
        container = manifest,
        backgroundImage = backgroundImage,
        header = ::testHeader,
        hero = ::testHero,
        callToAction = ::testCta,
    )

    @Composable
    private fun BoxScope.SnapshotTractPage(
        page: TractPage,
        pageState: TractPageState = TractPageState(page),
        contentInsets: PaddingValues = PaddingValues(top = PseudoAppBarHeight),
    ) {
        RenderTractPage(
            page,
            contentInsets = contentInsets,
            state = state,
            pageState = pageState,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(PseudoAppBarHeight)
                .background(page.manifest.navBarColor.toComposeColor())
        )
    }

    @Composable
    private fun BoxScope.BottomInsetBar(height: Dp) = Box(
        Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .height(height)
            .background(Color.Black.copy(alpha = 0.7f))
    )

    // Paparazzi reuses the per-thread AndroidUiDispatcher singleton across tests, so any dispatcher work left
    // scheduled at the end of a test must be drained before the next test runs to avoid leaking into it.
    @AfterTest
    fun drainAndroidUiDispatcher() = AndroidUiDispatcherUtil.runScheduledDispatches()

    @Test
    fun `RenderTractPage() - Hero with stacked cards`() {
        contentSnapshot { SnapshotTractPage(testPage()) }

        state.showTips.value = true
        val bgPage = testPage(backgroundImage = "waterfall")
        contentSnapshot("Background - Tips") { SnapshotTractPage(bgPage) }

        val rtlPage = testPage(rtlManifest)
        contentSnapshot("RTL - Tips") { SnapshotTractPage(rtlPage) }
    }

    @Test
    fun `RenderTractPage() - Active first card with peek`() {
        val page = testPage()
        contentSnapshot {
            SnapshotTractPage(page, pageState = TractPageState(page).apply { navigateToCard(page.cards[0]) })
        }

        val rtlPage = testPage(rtlManifest)
        contentSnapshot("RTL") {
            val pageState = TractPageState(rtlPage).apply { navigateToCard(rtlPage.cards[0]) }
            SnapshotTractPage(rtlPage, pageState = pageState)
        }
    }

    @Test
    fun `RenderTractPage() - Active middle card`() {
        val page = testPage(backgroundImage = "waterfall")
        contentSnapshot {
            SnapshotTractPage(page, pageState = TractPageState(page).apply { navigateToCard(page.cards[1]) })
        }

        state.showTips.value = true
        contentSnapshot("Tips") {
            SnapshotTractPage(page, pageState = TractPageState(page).apply { navigateToCard(page.cards[1]) })
        }

        val rtlPage = testPage(rtlManifest, backgroundImage = "waterfall")
        contentSnapshot("RTL - Tips") {
            val pageState = TractPageState(rtlPage).apply { navigateToCard(rtlPage.cards[1]) }
            SnapshotTractPage(rtlPage, pageState = pageState)
        }
    }

    @Test
    fun `RenderTractPage() - Active last card shows call to action`() {
        val page = testPage()
        contentSnapshot {
            SnapshotTractPage(page, pageState = TractPageState(page).apply { navigateToCard(page.cards[2]) })
        }

        state.showTips.value = true
        val bgPage = testPage(backgroundImage = "waterfall")
        contentSnapshot("Background - Tips") {
            val pageState = TractPageState(bgPage).apply { navigateToCard(bgPage.cards[2]) }
            SnapshotTractPage(bgPage, pageState = pageState)
        }

        val rtlPage = testPage(rtlManifest)
        contentSnapshot("RTL - Tips") {
            val pageState = TractPageState(rtlPage).apply { navigateToCard(rtlPage.cards[2]) }
            SnapshotTractPage(rtlPage, pageState = pageState)
        }
    }

    @Test
    fun `RenderTractPage() - Revealed hidden card`() = contentSnapshot {
        val page = testPage(hiddenCards = setOf(1))
        SnapshotTractPage(page, pageState = TractPageState(page).apply { navigateToCard(page.cards[1]) })
    }

    @Test
    fun `RenderTractPage() - No cards shows call to action under hero`() {
        contentSnapshot { SnapshotTractPage(testPageWithoutCards()) }

        state.showTips.value = true
        val bgPage = testPageWithoutCards(backgroundImage = "waterfall")
        contentSnapshot("Background - Tips") { SnapshotTractPage(bgPage) }
    }

    @Test
    fun `RenderTractPage() - Content insets`() {
        val bottomInset = 48.dp
        val insets = PaddingValues(top = PseudoAppBarHeight, bottom = bottomInset)

        contentSnapshot {
            SnapshotTractPage(testPage(), contentInsets = insets)
            BottomInsetBar(bottomInset)
        }

        val bgPage = testPage(backgroundImage = "waterfall")
        contentSnapshot("Background") {
            SnapshotTractPage(bgPage, contentInsets = insets)
            BottomInsetBar(bottomInset)
        }
    }

    // region RenderTractPage() - Animations
    @Test
    fun `RenderTractPage() - Animation - Open first card`() = animatedContentSnapshot(end = 1_000) {
        val page = testPage()
        val pageState = TractPageState(page)
        SnapshotTractPage(page, pageState = pageState)

        val touchRobot = rememberTouchRobot()
        LaunchedEffect(Unit) {
            delay(300.milliseconds)
            touchRobot.onNode(hasText("Card 1")).performGesture { click() }
        }
    }

    @Test
    fun `RenderTractPage() - Animation - Dismiss card`() = animatedContentSnapshot(end = 1_000) {
        val page = testPage()
        val pageState = TractPageState(page).apply { navigateToCard(page.cards[0]) }
        SnapshotTractPage(page, pageState = pageState)

        val touchRobot = rememberTouchRobot()
        LaunchedEffect(Unit) {
            delay(300.milliseconds)
            touchRobot.onNode(hasText("Card 1")).performGesture { click() }
        }
    }

    @Test
    fun `RenderTractPage() - Animation - Next card`() = animatedContentSnapshot(end = 1_300) {
        val page = testPage()
        val pageState = TractPageState(page).apply { navigateToCard(page.cards[1]) }
        SnapshotTractPage(page, pageState = pageState)

        val touchRobot = rememberTouchRobot()
        LaunchedEffect(Unit) {
            delay(300.milliseconds)
            touchRobot.onNode(hasText(nextText) and hasAnySibling(hasText("2/3"))).performGesture { click() }
        }
    }

    @Test
    fun `RenderTractPage() - Animation - Previous card`() = animatedContentSnapshot(end = 1_000) {
        val page = testPage()
        val pageState = TractPageState(page).apply { navigateToCard(page.cards[1]) }
        SnapshotTractPage(page, pageState = pageState)

        val touchRobot = rememberTouchRobot()
        LaunchedEffect(Unit) {
            delay(300.milliseconds)
            touchRobot.onNode(hasText(previousText) and hasAnySibling(hasText("2/3"))).performGesture { click() }
        }
    }

    @Test
    fun `RenderTractPage() - Animation - Bounce hint`() = animatedContentSnapshot(end = 3_200) {
        val page = testPage()
        SnapshotTractPage(page, pageState = TractPageState(page).apply { isBounceFirstCard = true })
    }
    // endregion RenderTractPage() - Animations
}
