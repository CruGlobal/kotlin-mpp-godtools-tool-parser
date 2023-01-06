package org.cru.godtools.shared.user.activity.internal.test

import com.github.ajalt.colormath.Color
import com.github.ajalt.colormath.model.RGBInt
import org.cru.godtools.shared.user.activity.model.IconColors

internal fun assertEquals(expected: Color, actual: Color) {
    kotlin.test.assertEquals(RGBInt.convert(expected), RGBInt.convert(actual))
}

internal fun assertEquals(expected: IconColors, actual: IconColors) {
    assertEquals(expected.light, actual.light)
    assertEquals(expected.dark, actual.dark)
    assertEquals(expected.containerLight, actual.containerLight)
    assertEquals(expected.containerDark, actual.containerDark)
}
