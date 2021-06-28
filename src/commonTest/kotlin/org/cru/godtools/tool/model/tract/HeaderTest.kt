package org.cru.godtools.tool.model.tract

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.internal.runBlockingTest
import org.cru.godtools.tool.model.Manifest
import org.cru.godtools.tool.model.TestColors
import org.cru.godtools.tool.model.primaryColor
import org.cru.godtools.tool.model.stylesParent
import org.cru.godtools.tool.model.tips.Tip
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunOnAndroidWith(AndroidJUnit4::class)
class HeaderTest : UsesResources("model/tract") {
    @Test
    fun testParseHeader() = runBlockingTest {
        val header = assertNotNull(TractPage(Manifest(), null, getTestXmlParser("header.xml")).header)
        assertEquals("5", header.number!!.text)
        assertEquals("title", header.title!!.text)
        assertEquals(TestColors.RED, header.backgroundColor)
        assertEquals("header-tip", header.tipId)
    }

    @Test
    fun testParseHeaderDefaults() = runBlockingTest {
        val page = TractPage(Manifest(), null, getTestXmlParser("header_defaults.xml"))
        val header = assertNotNull(page.header)

        assertEquals(page.primaryColor, header.backgroundColor)
        assertEquals(page.primaryTextColor, header.textColor)
        assertNull(header.number)
        assertNull(header.title)
        assertNull(header.tipId)
    }

    @Test
    fun testBackgroundColorBehavior() {
        val header = Header(backgroundColor = TestColors.GREEN)

        with(null as Header?) { assertEquals(stylesParent.primaryColor, backgroundColor) }
        with(header as Header?) { assertEquals(TestColors.GREEN, backgroundColor) }
        with(header) { assertEquals(TestColors.GREEN, backgroundColor) }
    }

    @Test
    fun testTipProperty() {
        val manifest = Manifest(tips = { listOf(Tip(it, "tip1")) })
        val header = Header(TractPage(manifest), tip = "tip1")
        assertEquals(manifest.findTip("tip1")!!, header.tip)
    }
}
