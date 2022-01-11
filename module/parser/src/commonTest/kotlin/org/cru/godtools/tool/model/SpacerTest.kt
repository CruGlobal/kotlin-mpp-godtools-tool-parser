package org.cru.godtools.tool.model

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.cru.godtools.tool.internal.AndroidJUnit4
import org.cru.godtools.tool.internal.RunOnAndroidWith
import org.cru.godtools.tool.internal.UsesResources
import org.cru.godtools.tool.model.Spacer.Mode.Companion.toModeOrNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

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
