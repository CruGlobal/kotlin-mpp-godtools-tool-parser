package org.cru.godtools.tool.internal

// region AndroidX Annotations
actual typealias AndroidColorInt = androidx.annotation.ColorInt
actual typealias AndroidDimension = androidx.annotation.Dimension
actual typealias VisibleForTesting = androidx.annotation.VisibleForTesting
// endregion AndroidX Annotations

// region IOException
// HACK: expect/actual doesn't currently allow more permissive visibility on actual
//       see: https://youtrack.jetbrains.com/issue/KT-37316
@Suppress("ACTUAL_WITHOUT_EXPECT")
internal actual typealias IOException = java.io.IOException
@Suppress("ACTUAL_WITHOUT_EXPECT")
internal actual typealias FileNotFoundException = java.io.FileNotFoundException
// endregion IOException

// region RestrictTo
// HACK: workaround a couple bugs
//       ACTUAL_WITHOUT_EXPECT: https://youtrack.jetbrains.com/issue/KT-37316
//       NO_ACTUAL_CLASS_MEMBER_FOR_EXPECTED_CLASS: https://youtrack.jetbrains.com/issue/KT-20900
@Suppress("ACTUAL_WITHOUT_EXPECT", "NO_ACTUAL_CLASS_MEMBER_FOR_EXPECTED_CLASS")
internal actual typealias RestrictTo = androidx.annotation.RestrictTo
@Suppress("ACTUAL_WITHOUT_EXPECT")
internal actual typealias RestrictToScope = androidx.annotation.RestrictTo.Scope
// endregion RestrictTo
