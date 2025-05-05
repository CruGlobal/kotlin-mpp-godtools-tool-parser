package org.cru.godtools.shared.tool.parser.model.tract

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.TestColors
import org.cru.godtools.shared.tool.parser.model.primaryColor
import org.cru.godtools.shared.tool.parser.model.stylesParent
import org.cru.godtools.shared.tool.parser.model.tips.Tip
import org.cru.godtools.shared.tool.parser.model.toPlatformColor

@RunOnAndroidWith(AndroidJUnit4::class)
class HeaderTest : UsesResources("model/tract") {
    @Test
    fun testParseHeader() = runTest {
        val header = assertNotNull(TractPage(Manifest(), null, getTestXmlParser("header.xml")).header)
        assertEquals("5", header.number!!.text)
        assertEquals("title", header.title!!.text)
        assertEquals(TestColors.RED.toPlatformColor(), header.backgroundColor)
        assertEquals("header-tip", header.tipId)
    }

    @Test
    fun testParseHeaderDefaults() = runTest {
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
        val header = Header(backgroundColor = TestColors.GREEN.toPlatformColor())

        with(null as Header?) { assertEquals(stylesParent.primaryColor, backgroundColor) }
        with(header as Header?) { assertEquals(TestColors.GREEN.toPlatformColor(), backgroundColor) }
        with(header) { assertEquals(TestColors.GREEN.toPlatformColor(), backgroundColor) }
    }

    @Test
    fun testTipProperty() {
        val manifest = Manifest(tips = { listOf(Tip(it, "tip1")) })
        val header = Header(TractPage(manifest), tip = "tip1")
        assertEquals(manifest.findTip("tip1")!!, header.tip)
    }
}
