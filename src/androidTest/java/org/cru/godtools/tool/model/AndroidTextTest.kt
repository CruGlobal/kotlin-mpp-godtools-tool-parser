package org.cru.godtools.tool.model

import android.graphics.Typeface
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

    @Test
    fun testTypefaceStyle() {
        assertEquals(Typeface.NORMAL, Text(Manifest()).typefaceStyle)
        assertEquals(Typeface.BOLD, Text(Manifest(), textStyles = setOf(Text.Style.BOLD)).typefaceStyle)
        assertEquals(Typeface.ITALIC, Text(Manifest(), textStyles = setOf(Text.Style.ITALIC)).typefaceStyle)
        assertEquals(
            Typeface.BOLD_ITALIC,
            Text(Manifest(), textStyles = setOf(Text.Style.BOLD, Text.Style.ITALIC)).typefaceStyle
        )
    }
}
