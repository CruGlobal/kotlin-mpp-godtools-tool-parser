package org.cru.godtools.tool.model

import com.github.ajalt.colormath.RGB
import kotlin.test.Test
import kotlin.test.assertEquals

class JsColorTest {
    @Test
    fun testToPlatformColor() {
        assertEquals("rgba(1, 2, 3, .5)", RGB(1, 2, 3, .5f).toPlatformColor())
    }
}
