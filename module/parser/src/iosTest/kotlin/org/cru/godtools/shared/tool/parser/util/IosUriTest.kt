package org.cru.godtools.shared.tool.parser.util

import kotlin.test.Test
import kotlin.test.assertNull

class IosUriTest {
    @Test
    fun testToAbsoluteUriOrNullInvalid() {
        assertNull("Bible.com/bible/111/JHN.1.NIV\n".toAbsoluteUriOrNull())
    }
}
