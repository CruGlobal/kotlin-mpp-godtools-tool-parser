package org.cru.godtools.shared.tool.parser.internal

// region AndroidX Annotations
actual typealias AndroidColorInt = androidx.annotation.ColorInt
actual typealias AndroidDimension = androidx.annotation.Dimension
// endregion AndroidX Annotations

// region IOException
// HACK: expect/actual doesn't currently allow more permissive visibility on actual
//       see: https://youtrack.jetbrains.com/issue/KT-37316
@Suppress("ACTUAL_WITHOUT_EXPECT")
internal actual typealias IOException = java.io.IOException
@Suppress("ACTUAL_WITHOUT_EXPECT")
internal actual typealias FileNotFoundException = java.io.FileNotFoundException
// endregion IOException
