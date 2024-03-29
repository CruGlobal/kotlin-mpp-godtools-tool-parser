package org.cru.godtools.shared.tool.parser.model.page

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Spacer
import org.cru.godtools.shared.tool.parser.xml.XmlPullParserException

@RunOnAndroidWith(AndroidJUnit4::class)
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
    // endregion Parse XML
}
