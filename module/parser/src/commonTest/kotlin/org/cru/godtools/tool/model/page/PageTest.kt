package org.cru.godtools.tool.model.page

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.internal.runBlockingTest
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.xml.XmlPullParserException
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

@RunOnAndroidWith(AndroidJUnit4::class)
class PageTest : UsesResources("model/page") {
    // region Page.parse()
    @Test
    fun testParseInvalidPageType() = runBlockingTest {
        assertNull(Page.parse(Manifest(), null, getTestXmlParser("page_invalid_type.xml")))
    }

    @Test
    fun testParseInvalidPageNamespace() = runBlockingTest {
        assertFailsWith(XmlPullParserException::class) {
            Page.parse(Manifest(), null, getTestXmlParser("page_invalid_namespace.xml"))
        }
    }
    // endregion Page.parse()
}
