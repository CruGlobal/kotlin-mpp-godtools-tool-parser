package org.cru.godtools.tool.model

import android.view.Gravity
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class AndroidTextTest {
    @Test
    fun testAlignGravity() {
        assertEquals(Gravity.START, Text.Align.START.gravity)
        assertEquals(Gravity.CENTER_HORIZONTAL, Text.Align.CENTER.gravity)
        assertEquals(Gravity.END, Text.Align.END.gravity)
    }
}
