package org.cru.godtools.shared.renderer

import androidx.compose.ui.unit.Dp
import kotlin.test.assertEquals

private const val DEFAULT_TOLERANCE = 0.00001f

internal fun assertEquals(expected: Dp, actual: Dp, message: String? = null) =
    assertEquals(expected, actual, DEFAULT_TOLERANCE, message)

internal fun assertEquals(expected: Dp, actual: Dp, absoluteTolerance: Float, message: String? = null) =
    assertEquals(expected.value, actual.value, absoluteTolerance, message)
