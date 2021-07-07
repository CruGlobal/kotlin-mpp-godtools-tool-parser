package org.cru.godtools.tool.internal

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

@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Retention(AnnotationRetention.BINARY)
expect annotation class AndroidDimension(val unit: Int)
internal const val DP = 0
// endregion Android Annotations

// region IOException
internal expect open class IOException(message: String? = null) : Exception
internal expect class FileNotFoundException(message: String? = null) : IOException
// endregion IOException

// region Parcelable/kotlin-parcelize
@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
expect annotation class Parcelize()
expect interface Parcelable
// endregion Parcelable/kotlin-parcelize

// region Testing Annotations
@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Retention(AnnotationRetention.BINARY)
expect annotation class VisibleForTesting()
// endregion Testing Annotations
