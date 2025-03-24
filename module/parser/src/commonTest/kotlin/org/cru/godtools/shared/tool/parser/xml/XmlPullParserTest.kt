package org.cru.godtools.shared.tool.parser.xml

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.internal.UsesResources

@RunOnAndroidWith(AndroidJUnit4::class)
class XmlPullParserTest : UsesResources("xml") {
    @Test
    fun `getAttributeValue - namespaces`() = runTest {
        val parser = getTestXmlParser("parser_attributes_namespaces.xml")
        assertEquals("none", parser.getAttributeValue("attr"))
        assertEquals("ns1", parser.getAttributeValue("ns1:", "attr"))
        assertEquals("ns2", parser.getAttributeValue("ns2:", "attr"))
    }
}
