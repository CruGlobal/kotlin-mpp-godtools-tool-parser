package org.cru.godtools.tool.model.page

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.internal.runBlockingTest
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.Spacer
import org.cru.godtools.tool.xml.XmlPullParserException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

@RunOnAndroidWith(AndroidJUnit4::class)
class ContentPageTest : UsesResources("model/page") {
    // region Parse XML
    @Test
    fun testParseContentPage() = runBlockingTest {
        with(ContentPage(Manifest(), "page_content.xml", getTestXmlParser("page_content.xml"))) {
            assertEquals(1, analyticsEvents.size)
            assertEquals(2, content.size)
            assertIs<Spacer>(content.first())
        }
    }

    @Test
    fun testParseContentPageInvalidType() = runBlockingTest {
        assertFailsWith(XmlPullParserException::class) {
            ContentPage(Manifest(), null, getTestXmlParser("page_invalid_type.xml"))
        }
    }
    // endregion Page.parse()
}
