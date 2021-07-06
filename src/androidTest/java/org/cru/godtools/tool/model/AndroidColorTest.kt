package org.cru.godtools.tool.model

import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ajalt.colormath.RGB
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class AndroidColorTest {
    @Test
    fun testColorGeneration() {
        val color = RGB(1, 2, 3, .5f).toColor()

        assertEquals(1, Color.red(color))
        assertEquals(2, Color.green(color))
        assertEquals(3, Color.blue(color))
        assertEquals(128, Color.alpha(color))
    }
}
