package org.cru.godtools.tool.model.tract

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.TestColors
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@RunOnAndroidWith(AndroidJUnit4::class)
class HeaderTest : UsesResources("model/tract") {
    @Test
    fun testParseHeader() {
        val header = assertNotNull(TractPage(Manifest(), null, getTestXmlParser("header.xml")).header)
        assertEquals("5", header.number!!.text)
        assertEquals("title", header.title!!.text)
        assertEquals(TestColors.RED, header.backgroundColor)
        assertEquals("header-tip", header.tipId)
    }
}
