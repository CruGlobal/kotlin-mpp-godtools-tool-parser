package org.cru.godtools.shared.tool.parser.model.tips

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.Manifest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class InlineTipTest : UsesResources("model/tips") {
    @Test
    fun verifyParseInlineTip() = runTest {
        val tip = InlineTip(Manifest(), getTestXmlParser("inline_tip.xml"))
        assertEquals("tip1", tip.id)
    }

    @Test
    fun verifyTipAccessor() {
        val manifest = Manifest(tips = { listOf(Tip(it, "tip1")) })
        val tip = manifest.findTip("tip1")!!

        val valid = InlineTip(manifest, "tip1")
        assertSame(tip, valid.tip)
        assertEquals(listOf(tip), valid.tips)

        val missing = InlineTip(manifest, "tip2")
        assertNull(missing.tip)
        assertEquals(emptyList(), missing.tips)
    }
}
