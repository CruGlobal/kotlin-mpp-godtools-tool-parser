package org.cru.godtools.tool.model.tips

import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.model.Manifest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

@RunOnAndroidWith(AndroidJUnit4::class)
class InlineTipTest : UsesResources("model/tips") {
    @Test
    fun verifyParseInlineTip() {
        val tip = InlineTip(Manifest(), getTestXmlParser("inline_tip.xml"))
        assertEquals("tip1", tip.id)
    }

    @Test
    fun verifyTipAccessor() {
        val manifest = Manifest(tips = { listOf(Tip(it, "tip1")) })
        val tip = manifest.findTip("tip1")!!

        val valid = InlineTip(manifest, "tip1")
        assertSame(tip, valid.tip)

        val missing = InlineTip(manifest, "tip2")
        assertNull(missing.tip)
    }
}
