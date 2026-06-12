package org.cru.godtools.shared.tool.parser.model

import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.ajalt.colormath.model.RGB
import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AndroidColorTest {
    @Test
    fun testToPlatformColor() {
        val color = RGB(.25, .5, .75, .5f).toPlatformColor()

        assertEquals(64, Color.red(color))
        assertEquals(128, Color.green(color))
        assertEquals(191, Color.blue(color))
        assertEquals(128, Color.alpha(color))
    }
}
