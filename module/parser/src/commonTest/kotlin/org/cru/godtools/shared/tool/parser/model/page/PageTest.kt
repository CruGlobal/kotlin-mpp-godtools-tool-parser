package org.cru.godtools.shared.tool.parser.model.page

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.cru.godtools.shared.tool.parser.internal.AndroidJUnit4
import org.cru.godtools.shared.tool.parser.internal.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.lesson.LessonPage
import org.cru.godtools.shared.tool.parser.model.tract.TractPage
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertSame

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class PageTest : UsesResources("model/page") {
    // region Page.parse()
    @Test
    fun testParseCardCollectionPage() = runTest {
        assertIs<CardCollectionPage>(
            Page.parse(Manifest(type = Manifest.Type.CYOA), null, getTestXmlParser("page_cardcollection.xml"))
        )
        assertNull(Page.parse(Manifest(type = Manifest.Type.LESSON), null, getTestXmlParser("page_cardcollection.xml")))
    }

    @Test
    fun testParseContentPage() = runTest {
        assertIs<ContentPage>(
            Page.parse(Manifest(type = Manifest.Type.CYOA), null, getTestXmlParser("page_content.xml"))
        )
        assertNull(Page.parse(Manifest(type = Manifest.Type.LESSON), null, getTestXmlParser("page_content.xml")))
    }

    @Test
    fun testParseLessonPage() = runTest {
        assertIs<LessonPage>(
            Page.parse(Manifest(type = Manifest.Type.LESSON), null, getTestXmlParser("../lesson/page.xml"))
        )
        assertNull(Page.parse(Manifest(type = Manifest.Type.TRACT), null, getTestXmlParser("../lesson/page.xml")))
    }

    @Test
    fun testParseTractPage() = runTest {
        assertIs<TractPage>(
            Page.parse(Manifest(type = Manifest.Type.TRACT), null, getTestXmlParser("../tract/page.xml"))
        )
        assertNull(Page.parse(Manifest(type = Manifest.Type.LESSON), null, getTestXmlParser("../tract/page.xml")))
    }

    @Test
    fun testParseInvalidPageType() = runTest {
        Manifest.Type.values().forEach {
            assertNull(Page.parse(Manifest(type = it), null, getTestXmlParser("page_invalid_type.xml")))
        }
    }

    @Test
    fun testParseInvalidPageNamespace() = runTest {
        Manifest.Type.values().forEach {
            assertNull(Page.parse(Manifest(type = it), null, getTestXmlParser("page_invalid_namespace.xml")))
        }
    }
    // endregion Page.parse()

    // region Page.parent
    @Test
    fun testParentPage() {
        val manifest = Manifest(
            pages = { listOf(ContentPage(it, id = "page1"), ContentPage(it, id = "page2", parentPage = "page1")) }
        )

        val page1 = manifest.findPage("page1")!!
        val page2 = manifest.findPage("page2")!!
        assertSame(page1, page2.parentPage)
    }
    // endregion Page.parent
}
