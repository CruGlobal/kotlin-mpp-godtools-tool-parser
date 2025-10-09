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
                    Text("Visible: ${lessonPagerState.visiblePages.toSet()}")

                    Button(
                        onClick = {
                            lessonPagerState.visiblePages += "page2"
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
}
