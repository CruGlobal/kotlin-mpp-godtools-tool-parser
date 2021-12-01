package org.cru.godtools.tool.model.page

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.internal.runBlockingTest
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.xml.XmlPullParserException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@RunOnAndroidWith(AndroidJUnit4::class)
class CardCollectionPageTest : UsesResources("model/page") {
    // region Parse XML
    @Test
    fun testParseCardCollectionPage() = runBlockingTest {
        with(CardCollectionPage(Manifest(), "file.ext", getTestXmlParser("page_cardcollection.xml"))) {
            assertEquals(1, analyticsEvents.size)
        }
    }

    @Test
    fun testParseCardCollectionPageInvalidType() = runBlockingTest {
        assertFailsWith(XmlPullParserException::class) {
            CardCollectionPage(Manifest(), null, getTestXmlParser("page_invalid_type.xml"))
        }
    }
    // endregion Parse XML
}
