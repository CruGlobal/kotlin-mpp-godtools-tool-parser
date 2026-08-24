package org.cru.godtools.shared.renderer.lesson

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.StateRestorationTester
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.BaseRendererTest
import org.cru.godtools.shared.renderer.internal.test.IgnoreOnIos
import org.cru.godtools.shared.tool.parser.model.EventId
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Manifest.Type
import org.cru.godtools.shared.tool.parser.model.lesson.LessonPage

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTestApi::class)
class LessonPagerStateTest : BaseRendererTest() {
    @Test
    fun `rememberLessonPagerState - Initial Page`() = runComposeUiTest {
        val manifest = Manifest(type = Type.LESSON) {
            listOf(
                LessonPage(it),
                LessonPage(it),
                LessonPage(it),
            )
        }

        setContent {
            ProvideTestCompositionLocals {
                val pagerState = rememberLessonPagerState(manifest, initialPage = 1)
                Text("Page: ${pagerState.pagerState.currentPage}")
            }
        }

        onNodeWithText("Page: 1").assertExists()
    }

    @Test
    fun `rememberLessonPagerState - Initial LessonPage - Hidden`() = runComposeUiTest {
        val manifest = Manifest(type = Type.LESSON) {
            listOf(
                LessonPage(it, id = "page1"),
                LessonPage(it, id = "page2", isHidden = true),
                LessonPage(it, id = "page3"),
            )
        }

        setContent {
            ProvideTestCompositionLocals {
                val pagerState = rememberLessonPagerState(manifest, initialPage = manifest.lessonPage("page2"))
                Text("Page: ${pagerState.pagerState.currentPage} of ${pagerState.pages.size}")
            }
        }

        onNodeWithText("Page: 1 of 3").assertExists()
    }

    // region LessonPagerState(manifest, currentPage: LessonPage?)
    @Test
    fun `LessonPagerState - initialPage - starts on the initial page`() {
        val manifest = Manifest(type = Type.LESSON) {
            listOf(
                LessonPage(it, id = "page1"),
                LessonPage(it, id = "page2"),
            )
        }

        val state = LessonPagerState(manifest, currentPage = manifest.lessonPage("page2"))
        assertEquals("page2", state.settledPage?.id, "the pager should start on the initial page")
    }

    @Test
    fun `LessonPagerState - initialPage - hidden initial page should be visible`() {
        val manifest = Manifest(type = Type.LESSON) {
            listOf(
                LessonPage(it, id = "page1"),
                LessonPage(it, id = "page2", isHidden = true),
                LessonPage(it, id = "page3"),
            )
        }

        val state = LessonPagerState(manifest, currentPage = manifest.lessonPage("page2"))
        assertEquals(
            listOf("page1", "page2", "page3"),
            state.pages.map { it.id },
            "a hidden initial page should be initially visible",
        )
        assertEquals("page2", state.settledPage?.id, "the pager should start on the hidden initial page")
    }

    @Test
    fun `LessonPagerState - initialPage - null initial page starts on the first page`() {
        val manifest = Manifest(type = Type.LESSON) {
            listOf(
                LessonPage(it, id = "page1"),
                LessonPage(it, id = "page2"),
            )
        }

        val state = LessonPagerState(manifest, currentPage = null)
        assertEquals("page1", state.settledPage?.id)
    }

    @Test
    fun `LessonPagerState - initialPage - initial page missing from the manifest starts on the first page`() {
        val manifest = Manifest(type = Type.LESSON) {
            listOf(
                LessonPage(it, id = "page1"),
                LessonPage(it, id = "page2"),
            )
        }

        val state = LessonPagerState(manifest, currentPage = LessonPage(id = "other"))
        assertEquals("page1", state.settledPage?.id)
    }
    // endregion LessonPagerState(manifest, currentPage: LessonPage?)

    // region settledPage
    @Test
    fun `settledPage - returns the page the pager is settled on`() {
        val manifest = Manifest(type = Type.LESSON) {
            listOf(
                LessonPage(it, id = "page1"),
                LessonPage(it, id = "page2"),
            )
        }

        val state = LessonPagerState(manifest, currentPage = 1)
        assertEquals("page2", state.settledPage?.id, "settledPage should be the page at the pager's settled index")
    }

    @Test
    fun `settledPage - indexes into visible pages only`() {
        val manifest = Manifest(type = Type.LESSON) {
            listOf(
                LessonPage(it, id = "page1"),
                LessonPage(it, id = "page2", isHidden = true),
                LessonPage(it, id = "page3"),
            )
        }

        val state = LessonPagerState(manifest, currentPage = 1)
        assertEquals("page3", state.settledPage?.id, "hidden pages are excluded from pages, shifting the index")

        state.revealedPages += "page2"
        assertEquals("page2", state.settledPage?.id, "settledPage should update when a hidden page becomes visible")
    }

    @Test
    fun `settledPage - null when there are no pages`() {
        val state = LessonPagerState()
        assertNull(state.settledPage, "settledPage should be null when the pager has no pages")
    }

    @Test
    fun `settledPage - null when the settled index is beyond the available pages`() {
        val manifest = Manifest(type = Type.LESSON) {
            listOf(
                LessonPage(it, id = "page1"),
                LessonPage(it, id = "page2"),
            )
        }

        val state = LessonPagerState(manifest, currentPage = 1)
        state.updatePages(emptyList())
        assertNull(state.settledPage, "settledPage should be null when pages shrink below the settled index")
    }
    // endregion settledPage

    @Test
    @IgnoreOnIos // TODO: https://youtrack.jetbrains.com/issue/CMP-6836
    fun `rememberLessonPagerState - Saveable`() = runComposeUiTest {
        val event1 = EventId(name = "content_event")
        val manifest = Manifest(type = Type.LESSON) {
            listOf(
                LessonPage(it, id = "page1"),
                LessonPage(it, id = "page2", isHidden = true, listeners = setOf(event1)),
                LessonPage(it, id = "page3"),
            )
        }

        val restorationTester = StateRestorationTester(this)
        restorationTester.setContent {
            ProvideTestCompositionLocals {
                val coroutineScope = rememberCoroutineScope()
                val lessonPagerState = rememberLessonPagerState(manifest, 0)

                Column {
                    HorizontalPager(lessonPagerState.pagerState) {}

                    Text("Current Page: ${lessonPagerState.pagerState.currentPage}")
                    val pageCount by remember { derivedStateOf { lessonPagerState.pagerState.pageCount } }
                    Text("Page Count: $pageCount")
                    Text("Visible: ${lessonPagerState.revealedPages.toSet()}")

                    Button(
                        onClick = {
                            lessonPagerState.revealedPages += "page2"
                            coroutineScope.launch { lessonPagerState.pagerState.animateScrollToPage(1) }
                        }
                    ) {
                        Text("Show Page 2")
                    }
                }
            }
        }

        onNodeWithText("Show Page 2").assertExists().performClick()
        onNodeWithText("Current Page: 1").assertExists()
        onNodeWithText("Page Count: 3").assertExists()
        onNodeWithText("Visible: [page2]").assertExists()

        restorationTester.emulateSaveAndRestore()
        onNodeWithText("Current Page: 1").assertExists()
        onNodeWithText("Page Count: 3").assertExists()
        onNodeWithText("Visible: [page2]").assertExists()
    }

    private fun Manifest.lessonPage(id: String) = pages.filterIsInstance<LessonPage>().first { it.id == id }
}
