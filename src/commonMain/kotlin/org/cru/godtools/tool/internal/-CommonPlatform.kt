package org.cru.godtools.tool.internal

// region Parcelable/kotlin-parcelize
@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
expect annotation class Parcelize()
expect interface Parcelable
// endregion Parcelable/kotlin-parcelize

// region Android Annotations
@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.FIELD
)
expect annotation class AndroidColorInt()
// endregion Android Annotations

@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Retention(AnnotationRetention.BINARY)
expect annotation class VisibleForTesting()
