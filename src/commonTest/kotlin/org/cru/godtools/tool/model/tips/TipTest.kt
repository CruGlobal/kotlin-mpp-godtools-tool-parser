package org.cru.godtools.tool.model.tips

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.model.Manifest
import kotlin.test.Test
import kotlin.test.assertEquals

@RunOnAndroidWith(AndroidJUnit4::class)
class TipTest : UsesResources("model/tips") {
    @Test
    fun verifyParse() {
        val tip = Tip(Manifest(), "name", getTestXmlParser("tip.xml"))
        assertEquals("name", tip.id)
        assertEquals(Tip.Type.ASK, tip.type)
    }
}
