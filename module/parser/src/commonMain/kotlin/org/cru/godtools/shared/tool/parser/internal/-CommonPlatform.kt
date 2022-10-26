package org.cru.godtools.shared.tool.parser.internal

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

@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Target(AnnotationTarget.CONSTRUCTOR, AnnotationTarget.PROPERTY_GETTER)
internal expect annotation class RestrictTo(vararg val value: RestrictToScope)
internal expect enum class RestrictToScope { TESTS, SUBCLASSES }
// endregion Android Annotations

// region IOException
internal expect open class IOException(message: String? = null) : Exception
internal expect class FileNotFoundException(message: String? = null) : IOException
// endregion IOException

// region Testing Annotations
@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Retention(AnnotationRetention.BINARY)
expect annotation class VisibleForTesting()
// endregion Testing Annotations
