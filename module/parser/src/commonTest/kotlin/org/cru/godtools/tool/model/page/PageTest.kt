package org.cru.godtools.tool.model.page

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.internal.runBlockingTest
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.lesson.LessonPage
import org.cru.godtools.tool.model.tract.TractPage
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNull

@RunOnAndroidWith(AndroidJUnit4::class)
class PageTest : UsesResources("model/page") {
    // region Page.parse()
    @Test
    fun testParseLessonPage() = runBlockingTest {
        assertIs<LessonPage>(
            Page.parse(Manifest(type = Manifest.Type.LESSON), null, getTestXmlParser("../lesson/page.xml"))
        )
        assertNull(Page.parse(Manifest(type = Manifest.Type.TRACT), null, getTestXmlParser("../lesson/page.xml")))
    }

    @Test
    fun testParseTractPage() = runBlockingTest {
        assertIs<TractPage>(
            Page.parse(Manifest(type = Manifest.Type.TRACT), null, getTestXmlParser("../tract/page.xml"))
        )
        assertNull(Page.parse(Manifest(type = Manifest.Type.LESSON), null, getTestXmlParser("../tract/page.xml")))
    }

    @Test
    fun testParseInvalidPageType() = runBlockingTest {
        Manifest.Type.values().forEach {
            assertNull(Page.parse(Manifest(type = it), null, getTestXmlParser("page_invalid_type.xml")))
        }
    }

    @Test
    fun testParseInvalidPageNamespace() = runBlockingTest {
        Manifest.Type.values().forEach {
            assertNull(Page.parse(Manifest(type = it), null, getTestXmlParser("page_invalid_namespace.xml")))
        }
    }
    // endregion Page.parse()
}
