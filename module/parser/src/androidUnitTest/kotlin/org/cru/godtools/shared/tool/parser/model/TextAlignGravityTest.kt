package org.cru.godtools.shared.tool.parser.model

import android.view.Gravity
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TextAlignGravityTest {
    @Test
    fun testAlignGravity() {
        assertEquals(Gravity.START, Text.Align.START.gravity)
        assertEquals(Gravity.CENTER_HORIZONTAL, Text.Align.CENTER.gravity)
        assertEquals(Gravity.END, Text.Align.END.gravity)
    }
}
