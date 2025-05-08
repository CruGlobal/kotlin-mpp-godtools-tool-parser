package org.cru.godtools.shared.tool.util

import com.github.ajalt.colormath.Color
import kotlin.test.assertEquals

fun assertEquals(expected: Color, actual: Color, absoluteTolerance: Float = 0.0001f) {
    expected.toSRGB().toArray().zip(actual.toSRGB().toArray())
        .forEach { (e, a) -> assertEquals(e, a, absoluteTolerance) }
}
