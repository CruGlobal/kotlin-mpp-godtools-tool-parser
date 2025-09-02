package org.cru.godtools.shared.renderer.lesson

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.BaseRendererTest
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Manifest.Type
import org.cru.godtools.shared.tool.parser.model.lesson.LessonPage

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class RenderLessonTest : BaseRendererTest() {
    private companion object {
        fun hasCurrentPage(index: Int) = SemanticsMatcher("currentPage == $index") {
            it.config.getOrElseNullable(LessonPagerState) { null }?.currentPage == index
        }

        fun hasPageCount(count: Int) = SemanticsMatcher("pageCount == $count") {
            it.config.getOrElseNullable(LessonPagerState) { null }?.pageCount == count
        }

        fun hasPageId(id: String) = SemanticsMatcher("pageId == $id") {
            it.config.getOrElseNullable(LessonPageId) { null } == id
        }

        fun SemanticsNodeInteractionsProvider.onPager() = onNodeWithTag(TestTagLessonPager)
        fun SemanticsNodeInteractionsProvider.onLessonPage(id: String) =
            onNode(hasTestTag(TestTagLessonPage) and hasPageId(id))
    }

    @Test
    fun `UI - Unsupported Type`() = runComposeUiTest {
        val manifest = Manifest(type = Type.TRACT)
        setContent {
            ProvideTestCompositionLocals {
                RenderLesson(manifest)
            }
        }
        onPager().assertDoesNotExist()
    }

    @Test
    fun `UI - HorizontalPager`() = runComposeUiTest {
        val manifest = Manifest(
            type = Type.LESSON,
            pages = {
                listOf(
                    LessonPage(it, id = "page1"),
                    LessonPage(it, id = "page2"),
                )
            },
        )
        setContent {
            ProvideTestCompositionLocals {
                RenderLesson(manifest)
            }
        }

        onPager()
            .assert(hasPageCount(2))
            .assert(hasCurrentPage(0))
        onLessonPage("page1").assertIsDisplayed()
        onLessonPage("page2").assertDoesNotExist()
    }

    @Test
    fun `UI - HorizontalPager - Hide Hidden Pages`() = runComposeUiTest {
        val manifest = Manifest(
            type = Type.LESSON,
            pages = {
                listOf(
                    LessonPage(id = "page1", isHidden = true),
                    LessonPage(id = "page2", isHidden = false),
                    LessonPage(id = "page3", isHidden = true),
                )
            },
        )
        setContent {
            ProvideTestCompositionLocals {
                RenderLesson(manifest)
            }
        }

        onPager().assert(hasPageCount(1) and hasCurrentPage(0))
        onLessonPage("page2").assertIsDisplayed()
    }
}
