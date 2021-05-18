package org.cru.godtools.tool.internal

// region Parcelable/kotlin-parcelize
@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
expect annotation class Parcelize()
expect interface Parcelable
// endregion Parcelable/kotlin-parcelize
