package org.cru.godtools.shared.interop.colormath

import com.github.ajalt.colormath.Color
import kotlin.experimental.ExperimentalObjCRefinement
import org.cru.godtools.shared.common.internal.colormath.toUIColor

@ShouldRefineInSwift
@OptIn(ExperimentalObjCRefinement::class)
fun Color.toUIColor() = toUIColor()
