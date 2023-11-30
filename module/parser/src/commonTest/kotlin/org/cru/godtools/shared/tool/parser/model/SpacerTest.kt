package org.cru.godtools.shared.tool.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.tool.parser.internal.UsesResources
import org.cru.godtools.shared.tool.parser.model.Spacer.Mode.Companion.toModeOrNull

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class SpacerTest : UsesResources() {
    @Test
    fun testParseSpacerDefaults() = runTest {
        val spacer = Spacer(Manifest(), getTestXmlParser("spacer.xml"))
        assertNotNull(spacer)
        assertEquals(Spacer.Mode.AUTO, spacer.mode)
        assertEquals(0, spacer.height)
    }

    @Test
    fun testParseSpacerFixedHeight() = runTest {
        val spacer = Spacer(Manifest(), getTestXmlParser("spacer_fixed.xml"))
        assertEquals(Spacer.Mode.FIXED, spacer.mode)
        assertEquals(123, spacer.height)
    }

    @Test
    fun testParseMode() {
        assertEquals(Spacer.Mode.AUTO, "auto".toModeOrNull())
        assertEquals(Spacer.Mode.FIXED, "fixed".toModeOrNull())
        assertNull("ajsdkf".toModeOrNull())
    }

    @Test
    fun testTestConstructor() {
        val spacer = Spacer(Manifest(), Spacer.Mode.FIXED, 10)
        assertEquals(Spacer.Mode.FIXED, spacer.mode)
        assertEquals(10, spacer.height)
    }
}
