package org.cru.godtools.shared.tool.parser.model.page

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.cru.godtools.shared.tool.parser.internal.AndroidJUnit4
import org.cru.godtools.shared.tool.parser.internal.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Spacer
import org.cru.godtools.shared.tool.parser.xml.XmlPullParserException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ContentPageTest : UsesResources("model/page") {
    // region Parse XML
    @Test
    fun testParseContentPage() = runTest {
        with(ContentPage(Manifest(), "page_content.xml", getTestXmlParser("page_content.xml"))) {
            assertEquals(1, analyticsEvents.size)
            assertEquals(2, content.size)
            assertIs<Spacer>(content.first())
        }
    }

    @Test
    fun testParseContentPageInvalidType() = runTest {
        assertFailsWith(XmlPullParserException::class) {
            ContentPage(Manifest(), null, getTestXmlParser("page_invalid_type.xml"))
        }
    }
    // endregion Page.parse()
}
