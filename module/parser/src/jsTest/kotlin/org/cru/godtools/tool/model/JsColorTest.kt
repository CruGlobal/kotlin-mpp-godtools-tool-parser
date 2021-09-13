package org.cru.godtools.tool.model

import com.github.ajalt.colormath.model.RGB
import kotlin.test.Test
import kotlin.test.assertEquals

class JsColorTest {
    @Test
    fun testToPlatformColor() {
        assertEquals("rgba(64, 128, 191, 0.5)", RGB(.25, .50, .75, .5f).toPlatformColor())
    }
}
