package org.cru.godtools.tool.model

import kotlin.test.Test
import kotlin.test.assertNull

class IosUriTest {
    @Test
    fun testToAbsoluteUriOrNullInvalid() {
        assertNull("Bible.com/bible/111/JHN.1.NIV\n".toAbsoluteUriOrNull())
    }
}
